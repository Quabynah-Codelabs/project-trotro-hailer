package dev.trotrohailer.shared

import dev.trotrohailer.shared.data.User
import dev.trotrohailer.shared.result.Response

interface UserRepository {
    fun getUser(id: String, refresh: Boolean): Response<User>
    fun getUsers(refresh: Boolean): Response<MutableList<User>>
}

//interface

