package ru.avtomaton.irz.app.client.api.users

import android.graphics.Bitmap
import retrofit2.Response
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.client.api.images.ImageRepository
import ru.avtomaton.irz.app.client.api.users.models.*
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
class UserRepository {

    private val imageRepository: ImageRepository = ImageRepository()

    suspend fun getMe(): User? {
        val userResponse: Response<UserDto>
        val positionsResponse: Response<List<PositionDto>>
        try {
            userResponse = IrzClient.usersApi.me()
            positionsResponse = IrzClient.usersApi.myUserPositions()
        } catch (ex: Throwable) {
            return null
        }
        if (!userResponse.isSuccessful || !positionsResponse.isSuccessful) {
            return null
        }
        return get(userResponse.body()!!, positionsResponse.body()!!)
    }

    suspend fun getUser(id: UUID) : User? {
        val userResponse: Response<UserDto>
        val positionsResponse: Response<List<PositionDto>>
        try {
            userResponse = IrzClient.usersApi.user(id)
            positionsResponse = IrzClient.usersApi.userPositions(id)
        } catch (ex: Throwable) {
            return null
        }
        if (!userResponse.isSuccessful || !positionsResponse.isSuccessful) {
            return null
        }
        return get(userResponse.body()!!, positionsResponse.body()!!)
    }

    private suspend fun get(userDto: UserDto, positionDtoList: List<PositionDto>): User {
        val image = if (userDto.imageId != null) imageRepository.getImage(userDto.imageId) else null
        return convert(userDto, positionDtoList, image)
    }

    private fun convert(userDto: UserDto, positionDtoList: List<PositionDto>, image: Bitmap?): User {
        return User(
            userDto.id,
            userDto.firstName,
            userDto.surname,
            userDto.patronymic.orEmpty(),
            userDto.birthday,
            image,
            userDto.aboutMyself ?: "...",
            userDto.myDoings ?: "...",
            userDto.skills ?: "...",
            userDto.subscribersCount,
            userDto.subscriptionsCount,
            userDto.isSubscription,
            userDto.email,
            userDto.isActiveAccount,
            roles(userDto),
            positions(positionDtoList)
        )
    }

    private fun roles(userDto: UserDto): List<String> {
        val result = mutableListOf<String>()
        userDto.roles.forEach {
            if (UserRoles.containsRole(it)) {
                result.add(it)
            }
        }
        return result
    }

    private fun positions(positionDtoList: List<PositionDto>): List<Position> {
        return positionDtoList
            .sortedByDescending { it.start }
            .map { Position(it.start, it.end, it.position.name) }
            .toList()
    }
}