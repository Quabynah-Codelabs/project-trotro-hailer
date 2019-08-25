package dev.trotrohailer.shared.datasource

import dev.trotrohailer.shared.result.Response

interface UserRepository<R> {
    suspend fun getUser(id: String, refresh: Boolean): Response<R>
    suspend fun getUsers(refresh: Boolean): Response<MutableList<R>>
}

