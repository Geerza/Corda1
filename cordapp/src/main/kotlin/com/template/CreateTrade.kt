package com.template

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.Command
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.*
import net.corda.core.utilities.ProgressTracker




// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class CreateTradeInitiator(val otherParty: Party,
                        val amount: Int) : FlowLogic<SignedTransaction>() {
//start CreateTradeInitiator otherParty: "O=BankB,L=New York,C=US", amountTrade: 123
    // run vaultQuery contractStateType: com.template.IOUState
    /** The progress tracker provides checkpoints indicating the progress of the flow to observers. */
    override val progressTracker = ProgressTracker()

    /** The flow logic is encapsulated within the call() method. */
    @Suspendable
    override fun call():SignedTransaction {
        // We retrieve the notary identity from the network map.
        val notary = serviceHub.networkMapCache.notaryIdentities[0]

        // We create the transaction components.
        val outputState = IOUState( ourIdentity, otherParty,amount)
        val command = Command(IOUContract.Create(), listOf(ourIdentity.owningKey, otherParty.owningKey))

        // We create a transaction builder and add the components.
        val txBuilder = TransactionBuilder(notary = notary)
                .addOutputState(outputState, IOUContract.ID)
                .addCommand(command)

        // Verifying the transaction.
        txBuilder.verify(serviceHub)

        // Signing the transaction.
        val signedTx = serviceHub.signInitialTransaction(txBuilder)

        // Creating a session with the other party.
        val otherPartySession = initiateFlow(otherParty)

        // Obtaining the counterparty's signature.
        val fullySignedTx = subFlow(CollectSignaturesFlow(signedTx, listOf(otherPartySession), CollectSignaturesFlow.tracker()))

        // Finalising the transaction.
        return subFlow(FinalityFlow(fullySignedTx))
    }

}

@InitiatedBy(CreateTradeInitiator::class)
// post API for responding
class CreateTradeResponder(val otherPartySession: FlowSession) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val signTransactionFlow = object : SignTransactionFlow(otherPartySession, SignTransactionFlow.tracker()) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data
            }
        }

        return subFlow(signTransactionFlow)
    }
}
