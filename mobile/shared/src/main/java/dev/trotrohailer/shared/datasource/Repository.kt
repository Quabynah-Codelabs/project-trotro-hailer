package dev.trotrohailer.shared.datasource

import dev.trotrohailer.shared.result.Response

/**
 * User repository
 */
interface UserRepository<R> {
    suspend fun getUser(id: String, refresh: Boolean): Response<R>
    suspend fun getUsers(refresh: Boolean): Response<MutableList<R>>
    suspend fun saveUser(user: R): Nothing?
    suspend fun updateUser(user: R): Nothing?
    suspend fun deleteUser(user: R): Nothing?
}

