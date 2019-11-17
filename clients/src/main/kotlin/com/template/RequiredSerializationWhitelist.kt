package com.cordaL3

import net.corda.core.serialization.SerializationWhitelist

class RequiredSerializationWhitelist : SerializationWhitelist {
    override val whitelist: List<Class<*>> = listOf(HashSet::class.java
            ,java.sql.Date::class.java
            ,java.util.Date::class.java
            ,java.time.Instant::class.java)
}