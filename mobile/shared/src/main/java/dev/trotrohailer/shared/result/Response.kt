package dev.trotrohailer.shared.result

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

val Response<*>.succeeded get() = this is Response.Success && data != null