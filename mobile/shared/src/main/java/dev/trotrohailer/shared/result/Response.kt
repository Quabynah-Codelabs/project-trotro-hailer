package dev.trotrohailer.shared.result

import dev.trotrohailer.shared.result.Response.Success

/**
 * Wrapper for callbacks
 */
sealed class Response<out R> {
    data class Success<out T>(val data: T?) : Response<T>()
    data class Error(val e: Exception) : Response<Nothing>()
    object Loading : Response<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$e]"
            Loading -> "Loading"
        }
    }
}

/**
 * `true` if [Result] is of type [Success] & holds non-null [Success.data].
 */
val Response<*>.succeeded
    get() = this is Success && data != null

fun <T> Response<T>.successOr(fallback: T): T {
    return (this as? Success<T>)?.data ?: fallback
}