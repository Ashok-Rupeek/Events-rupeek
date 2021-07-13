package com.rupeek.events_sdk.network.helper

import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
): ResultWrapper<T> {
    return try {
        ResultWrapper.Success(apiCall.invoke())
    } catch (throwable: Throwable) {
        when (throwable) {
            is IOException -> ResultWrapper.NetworkError
            is HttpException -> {
                val code = throwable.code()
                var retry = throwable.response()?.headers()?.get("Retry-After")
                retry = retry ?: "0"
                ResultWrapper.GenericError(code, throwable.message(), retry.toInt())
            }
            else -> {
                ResultWrapper.GenericError(null, null)
            }
        }
    }
}