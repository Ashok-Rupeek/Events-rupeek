package com.rupeek.events_sdk.models

data class UserEvents(
    val timeStamp: String,
    val eventName: String,
    val properties: HashMap<String, Any>? = null,
    val category: String? = null,
    val source: String? = null,
    val screenName: String? = null,
    val eventType: String
)