package com.template

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class ProductModel(val fromParty: String?,
                        val toParty: String?,
                        val amountTrade: String?,
                        val tradeDateTime: String?,
                        val linearId: String?)