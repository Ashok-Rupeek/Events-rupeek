package com.rupeek.events_sdk.network

import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST

interface EventsInterface {
    @POST("/api/v1/create")
    suspend fun postEmployees(@Body json: JsonObject)
}