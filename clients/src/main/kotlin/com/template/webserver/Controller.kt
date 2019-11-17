package com.template.webserver

import com.template.*
import net.corda.client.jackson.JacksonSupport
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.CordaX500Name
import net.corda.core.utilities.getOrThrow
import org.apache.commons.jexl3.parser.ParserConstants.req
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.template.IOUState




val SERVICE_NAMES = listOf("Notary", "Network Map Service")
/**
 * Define your API endpoints here.
 */
@CrossOrigin
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
// the "/" is root
class Controller(rpc: NodeRPCConnection) {

    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val proxy = rpc.proxy
    private val myLegalName = proxy.nodeInfo().legalIdentities.first().name //plus

    // predefined tokens as O, OU and so on, called as proxy
// Request API for creating transaction

    @PostMapping(value = "/trades/create", produces = arrayOf("application/json"))     //CreateTrade   CreateForm.js
    // Requesting defined Model
    private fun createTrade(req: RequestEntity<TradeModel>): ResponseEntity<String> {
        val to = req.body.toParty
        val x500Name = CordaX500Name.parse(to!!) // partyB will not empty, we are passing non empty value
        val otherParty = proxy.wellKnownPartyFromX500Name(x500Name)   // converting string value to Party type
        val flowHandle = proxy.startFlowDynamic(
                CreateTradeInitiator::class.java,
                otherParty,
                req.body.amount
        )
        val result = flowHandle.use { flowHandle.returnValue.getOrThrow() }
        //puls
        val rstate = result.coreTransaction.outputs[0].data as IOUState

        val resultModel = TradeModel(

                rstate.linearId.toString(),
                rstate.from.nameOrNull().toString(),
                rstate.to.nameOrNull().toString(),
                rstate.amount,
                rstate.tradeDate,
                rstate.status

                )

        val mapper = JacksonSupport.createNonRpcMapper()
        val json = mapper.writeValueAsString(resultModel)
        return ResponseEntity
                .status(201)
                .body(json)

    }

    @GetMapping(value = "me", produces = arrayOf("application/json"))
    fun whoami() = mapOf("me" to myLegalName)

    @GetMapping(value = "peers", produces = arrayOf("application/json"))
    fun getPeers(): Map<String, List<CordaX500Name>> {
        val nodeInfo = proxy.networkMapSnapshot()
        return mapOf("peers" to nodeInfo
                .map { it.legalIdentities.first().name }
                //filter out myself, notary and eventual network map started by driver
                .filter { it.organisation !in (SERVICE_NAMES + myLegalName.organisation) })
    }

    //ListTrade     GetTrades.js UI file
    @GetMapping(value = "/trades", produces = arrayOf("application/json"))
    // Request API for updating transaction
    private fun listTrade() : ResponseEntity<List<TradeModel>> {
        val flowHandle = proxy.startFlowDynamic(ListTrade::class.java)
        val result = flowHandle.use { flowHandle.returnValue.getOrThrow() }
        return  ResponseEntity.status(201)
                .body(result)
    }


    @GetMapping(value = "/trades/id", produces = arrayOf("application/json"))
    // Request API for searching trade

    //private fun searchTrade(@RequestParam tradeId: String) {
    fun searchTrade(@RequestParam tradeId:String): ResponseEntity<TradeModel> {
        logger.info("Accessing getTradeById api")
        //val linearId = UniqueIdentifier.fromString(req.body.tradeId!!)
        val flowHandle = proxy.startFlowDynamic(
                SearchTrade::class.java,
                tradeId
        )
            val result = flowHandle.use { flowHandle.returnValue.getOrThrow() }
            return ResponseEntity.status(201)
                    .body(result)
        }

    }


