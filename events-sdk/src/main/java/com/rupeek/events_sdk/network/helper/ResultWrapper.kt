package com.rupeek.events_sdk.network.helper

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T): ResultWrapper<T>()
    data class GenericError(val code: Int? = null, val error: String? = null, val retryTime:Int = 0): ResultWrapper<Nothing>()
    object NetworkError: ResultWrapper<Nothing>()
}