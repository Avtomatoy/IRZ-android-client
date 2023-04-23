package ru.avtomaton.irz.app.client.api.users

import android.graphics.Bitmap
import retrofit2.Response
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.client.Repository
import ru.avtomaton.irz.app.client.OpResult
import ru.avtomaton.irz.app.client.api.UserPositionsRepository
import ru.avtomaton.irz.app.client.api.images.ImageRepository
import ru.avtomaton.irz.app.client.api.users.models.*
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
object UserRepository : Repository() {

    private const val threeDots = "..."
    private var me: User? = null

    suspend fun getMe(fromCache: Boolean = false): OpResult<User> {
        if (fromCache && me != null) {
            return OpResult.Success(me!!)
        }
        val user = getAnyUser(
            { IrzClient.usersApi.getMe() },
            { UserPositionsRepository.getMyPositions() }
        )
        if (user.isOk) {
            me = user.value()
        }
        return user
    }

    suspend fun getUser(id: UUID): OpResult<User> {
        return getAnyUser(
            { IrzClient.usersApi.user(id) },
            { UserPositionsRepository.getUserPositions(id) }
        )
    }

    private suspend fun getAnyUser(
        userBlock: suspend () -> Response<UserDto>,
        positionsBlock: suspend () -> OpResult<List<Position>>
    ): OpResult<User> {
        return try {
            val positionsResult = positionsBlock.invoke()
            if (positionsResult.isFailure) {
                return OpResult.Failure()
            }
            val userResponse = userBlock.invoke()
            if (!userResponse.isSuccessful) {
                return OpResult.Failure()
            }
            OpResult.Success(convert(userResponse.body()!!, positionsResult.value()))
        } catch (ex: Throwable) {
            ex.printStackTrace()
            OpResult.Failure()
        }
    }

    private suspend fun convert(userDto: UserDto, positions: List<Position>): User {
        val isMe = getMyId()?.equals(userDto.id) ?: false
        return User(
            userDto.id,
            userDto.firstName,
            userDto.surname,
            userDto.patronymic.orEmpty(),
            userDto.birthday,
            getImage(userDto.imageId),
            userDto.aboutMyself ?: threeDots,
            userDto.myDoings ?: threeDots,
            userDto.skills ?: threeDots,
            userDto.subscribersCount,
            userDto.subscriptionsCount,
            userDto.isSubscription,
            userDto.email,
            userDto.isActiveAccount,
            userDto.roles,
            positions,
            isMe
        )
    }

    private suspend fun getMyId(): UUID? {
        return try {
            val response = IrzClient.usersApi.getMe()
            if (response.isSuccessful) response.body()!!.id else null
        } catch (ex: Throwable) {
            ex.printStackTrace()
            null
        }
    }

    private suspend fun getImage(imageId: UUID?): Bitmap? {
        if (imageId == null) {
            return null
        }
        val result = ImageRepository.getImage(imageId)
        return if (result.isOk) result.value() else null
    }

//    @Deprecated("deprecated")
//    suspend fun getMeOld(): User? {
//        val userResponse: Response<UserDto>
//        val positionsResponse: Response<List<PositionDto>>
//        try {
//            userResponse = IrzClient.usersApi.getMe()
//            positionsResponse = IrzClient.usersApi.myUserPositions()
//        } catch (ex: Throwable) {
//            return null
//        }
//        if (!userResponse.isSuccessful || !positionsResponse.isSuccessful) {
//            return null
//        }
//        return getOld(userResponse.body()!!, positionsResponse.body()!!)
//    }

//    @Deprecated("")
//    suspend fun getUserOld(id: UUID): User? {
//        val userResponse: Response<UserDto>
//        val positionsResponse: Response<List<PositionDto>>
//        try {
//            userResponse = IrzClient.usersApi.user(id)
//            positionsResponse = IrzClient.usersApi.userPositions(id)
//        } catch (ex: Throwable) {
//            return null
//        }
//        if (!userResponse.isSuccessful || !positionsResponse.isSuccessful) {
//            return null
//        }
//        return getOld(userResponse.body()!!, positionsResponse.body()!!)
//    }

//    }

    //    @Deprecated("")
//    private suspend fun getOld(userDto: UserDto, positionDtoList: List<PositionDto>): User {
//        val image =
//            if (userDto.imageId != null) ImageRepository.getImageOld(userDto.imageId) else null
//        return convertOld(userDto, positionDtoList, image)


//    @Deprecated("")
//    private fun convertOld(
//        userDto: UserDto,
//        positionDtoList: List<PositionDto>,
//        image: Bitmap?
//    ): User {
//        return User(
//            userDto.id,
//            userDto.firstName,
//            userDto.surname,
//            userDto.patronymic.orEmpty(),
//            userDto.birthday,
//            image,
//            userDto.aboutMyself ?: "...",
//            userDto.myDoings ?: "...",
//            userDto.skills ?: "...",
//            userDto.subscribersCount,
//            userDto.subscriptionsCount,
//            userDto.isSubscription,
//            userDto.email,
//            userDto.isActiveAccount,
//            roles(userDto),
//            positions(positionDtoList)
//        )
//    }

    //    @Deprecated("depreacated")
//    private fun positions(positionDtoList: List<PositionDto>): List<Position> {
//        return positionDtoList
//            .sortedByDescending { it.start }
//            .map { Position(it.start, it.end, it.positionInfo.name) }
//            .toList()
//    }
}