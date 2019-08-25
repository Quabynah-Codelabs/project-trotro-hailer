package dev.trotrohailer.shared.result

/**
 * Wrapper for callbacks
 */
sealed class Response<out R> {
    data class Success<out T>(val data: T?) : Response<T>()
    data class Error(val e: Exception) : Response<Nothing>()
    object Loading : Response<Nothing>()
}

val Response<*>.succeeded get() = this is Response.Success && data != null