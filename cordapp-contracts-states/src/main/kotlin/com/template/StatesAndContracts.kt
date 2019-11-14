package com.template

import net.corda.core.contracts.*
import net.corda.core.identity.*
import net.corda.core.transactions.LedgerTransaction
import net.corda.core.contracts.UniqueIdentifier
import java.time.DateTimeException
import java.time.LocalDateTime
import java.util.*

// ************
// * Contract *
// ************
class IOUContract : Contract {
    companion object {
        const val ID = "com.template.IOUContract"
            }

    // Our Create command.
    class Create : CommandData
    // Our List command
    class List : CommandData
    //Our Search command.
    class Search : CommandData

    override fun verify(tx: LedgerTransaction) {
        val command=tx.getCommand<CommandData>(0)
        var requiredSignature = command.signers
        val commandType =command.value
        //val tradeDateTime = CurrentDate
        if (commandType is Create){

        requireThat {
            // Constraints on the shape of the transaction.
            "No inputs should be consumed when issuing an IOU." using (tx.inputs.isEmpty())
            "There should be one output state of type IOUState." using (tx.outputs.size == 1)
            val output = tx.outputsOfType<IOUState>().single()
            "The lender and the borrower cannot be the same entity." using (output.from != output.to)

            // Constraints on the signers.
            val expectedSigners = listOf(output.to.owningKey, output.from.owningKey)
            "There must be two signers." using (command.signers.toSet().size == 2)
            "The borrower and lender must be signers." using (command.signers.containsAll(expectedSigners))
        }

    }
        else if (commandType is Search){

            requireThat {
                "There should be one output state of type IOUState." using (tx.outputs.size == 1)
                // IOU-specific constraints.
                val output = tx.outputsOfType<IOUState>().single()

            }
        }  else if (commandType is List){

            requireThat {
                "There should be one output state of type IOUState." using (tx.outputs.size == 1)
                // IOU-specific constraints.
                val output = tx.outputsOfType<IOUState>().single()

            }
        }
        }
    }


// *********
// * State *
// *********


//lender = from, borrower = to

class IOUState(val from: Party,
               val to: Party,
               val amount: Int,
               val tradeDate: Date = Date(),
               val status:String = "SUBMITTED",
               override val linearId: UniqueIdentifier = UniqueIdentifier()) : LinearState {

    override val participants get() = listOf(from, to)
}
