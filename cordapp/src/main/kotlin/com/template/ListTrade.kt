package com.template

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*
import net.corda.core.utilities.ProgressTracker
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.node.services.queryBy
import java.util.*

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC

class ListTrade : FlowLogic<List<TradeModel>>() {

    /** The progress tracker provides checkpoints indicating the progress of the flow to observers. */
    override val progressTracker = ProgressTracker()

    /** The flow logic is encapsulated within the call() method. */
    @Suspendable
    override fun call(): List<TradeModel> {
        val stateAndRef = serviceHub.vaultService.queryBy<IOUState>().states
        val input = stateAndRef.map{ it.state.data }
        return input.map { TradeModel(it.linearId.id.toString(),
                it.from.toString(),
                it.to.toString(),
                it.amount,
                it.tradeDate,
                it.status
                ) }
    }

}


