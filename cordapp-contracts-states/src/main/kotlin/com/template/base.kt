package com.template
import java.util.*
import net.corda.core.serialization.CordaSerializable

@CordaSerializable


data class TradeModel(val tradeId: String?,
                      val fromParty: String?,
                        val toParty: String?,
                        val amount: Int?,
                        val tradeDate: String?,
                        val status : String?
                        ) {

}