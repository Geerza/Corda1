package com.template

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.utilities.ProgressTracker
import net.corda.core.contracts.UniqueIdentifier

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
                it.from.toString(),   //bcoz Party is not type in react or js
                it.to.toString(),
                it.amount,
                it.tradeDate,
                it.status
                ) }
    }

}


