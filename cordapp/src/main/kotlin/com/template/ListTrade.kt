package com.template

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.Command
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.*
import net.corda.core.utilities.ProgressTracker
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC

class ListBank(val linearId: UniqueIdentifier) : FlowLogic<SignedTransaction>() {

    //start UpdateProductStatusFlow linearId: status: "Available"
    // run vaultQuery contractStateType: com.template.inputState
    /** The progress tracker provides checkpoints indicating the progress of the flow to observers. */
    override val progressTracker = ProgressTracker()

    /** The flow logic is encapsulated within the call() method. */
    @Suspendable
    override fun call(): SignedTransaction {
        // We retrieve the notary identity from the network map.
        val notary = serviceHub.networkMapCache.notaryIdentities[0]
       // val criteria = QueryCriteria.LinearStateQueryCriteria(null, listOf(linearId),status = Vault.StateStatus.UNCONSUMED)
        val criteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(linearId))
        val stateAndRef = serviceHub.vaultService.queryBy<IOUState>(criteria).states.single()
        val inputState = stateAndRef.state.data
        
        val  outputState = IOUState(inputState.from,inputState.to,inputState.amountTrade,inputState.tradeDateTime,"SETTLED",inputState.linearId)

        // We create the transaction components.
        val command = Command(IOUContract.List(), listOf(inputState.from.owningKey, inputState.to.owningKey))
        // We create a transaction builder and add the components.
        val txBuilder = TransactionBuilder(notary = notary)
                .addOutputState(outputState, IOUContract.ID)
                .addInputState(stateAndRef)
                .addCommand(command)

        // Verifying the transaction.
        txBuilder.verify(serviceHub)

        // Signing the transaction.
        val signedTx = serviceHub.signInitialTransaction(txBuilder)

            // creating a session with our party
      //  val ourPartySession =  initiateFlow(ourIdentity)
        val ourPartySession =  initiateFlow(inputState.from)

        // Obtaining the counterparty's signature.
        val fullySignedTx = subFlow(CollectSignaturesFlow(signedTx, listOf(ourPartySession), CollectSignaturesFlow.tracker()))

        // Finalising the transaction.
        return subFlow(FinalityFlow(fullySignedTx))
    }

}


@InitiatedBy(ListBank::class)
// post API for responding
class IOUFlowResponder1(val ourPartySession: FlowSession) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
            val signTransactionFlow = object : SignTransactionFlow(ourPartySession, SignTransactionFlow.tracker()) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data
            }
        }

        return subFlow(signTransactionFlow)
    }
}