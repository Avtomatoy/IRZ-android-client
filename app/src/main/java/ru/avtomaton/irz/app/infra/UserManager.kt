package ru.avtomaton.irz.app.infra

import ru.avtomaton.irz.app.client.api.users.UserRepository
import ru.avtomaton.irz.app.client.api.users.models.User

/**
 * @author Anton Akkuzin
 */
object UserManager {

    private val userRepository: UserRepository = UserRepository()
    private var user: User? = null

    suspend fun downloadInfo() {
        user = userRepository.getMe()!!
    }

    fun getInfo(): User? {
        return user
    }
}