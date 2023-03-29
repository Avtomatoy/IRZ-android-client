package ru.avtomaton.irz.app.client.api.users

import android.graphics.Bitmap
import retrofit2.Response
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.client.api.images.ImageRepository
import ru.avtomaton.irz.app.client.api.users.models.User
import ru.avtomaton.irz.app.client.api.users.models.UserDto
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
class UserRepository {

    private val imageRepository: ImageRepository = ImageRepository()

    suspend fun getMe(): User? {
        val response: Response<UserDto>
        try {
            response = IrzClient.usersApi.me();
        } catch (ex: Throwable) {
            return null
        }
        if (!response.isSuccessful) {
            return null
        }
        return get(response.body()!!)
    }

    suspend fun getUser(id: UUID) : User? {
        val response: Response<UserDto>
        try {
            response = IrzClient.usersApi.user(id)
        } catch (ex: Throwable) {
            return null
        }
        if (!response.isSuccessful) {
            return null
        }
        return get(response.body()!!)
    }

    private suspend fun get(userDto: UserDto): User {
        val image = if (userDto.imageId != null) imageRepository.getImage(userDto.imageId) else null
        return convert(userDto, image)
    }

    private fun convert(userDto: UserDto, image: Bitmap?): User {
        return User(
            userDto.id,
            userDto.firstName,
            userDto.surname,
            userDto.patronymic,
            userDto.birthday,
            image,
            userDto.aboutMyself,
            userDto.myDoings,
            userDto.skills,
            userDto.subscribersCount,
            userDto.subscriptionsCount,
            userDto.isSubscription,
            userDto.email,
            userDto.isActiveAccount
        )
    }
}