package com.template

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*
import net.corda.core.utilities.ProgressTracker
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import java.util.*

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class SearchTrade(val tradeId: UniqueIdentifier) : FlowLogic<TradeModel>() {

    /** The progress tracker provides checkpoints indicating the progress of the flow to observers. */
    override val progressTracker = ProgressTracker()

    /** The flow logic is encapsulated within the call() method. */
    @Suspendable
    override fun call() : TradeModel {

        val criteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(tradeId))
        val stateAndRef = serviceHub.vaultService.queryBy<IOUState>(criteria).states.single()
        val inputState = stateAndRef.state.data

        return TradeModel(inputState.linearId.id.toString(),
                inputState.from.toString(),
                inputState.to.toString(),
                inputState.amount,
                inputState.tradeDate,
                inputState.status
                )

    }

}
