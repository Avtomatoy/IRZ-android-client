package ru.avtomaton.irz.app.model.repository

import android.graphics.Bitmap
import retrofit2.Response
import ru.avtomaton.irz.app.activity.util.SearchParams
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.model.OpResult
import ru.avtomaton.irz.app.model.pojo.*
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
object UserRepository {

    private const val threeDots = "…"
    private var me: User? = null

    suspend fun updatePhoto(imageDto: ImageDto): Boolean {
        return try {
            val response = IrzClient.usersApi.updatePhoto(imageDto)
            response.isSuccessful
        } catch (ex: Throwable) {
            ex.printStackTrace()
            false
        }
    }

    suspend fun deletePhoto(): Boolean {
        return try {
            IrzClient.usersApi.deletePhoto().isSuccessful
        } catch (ex: Throwable) {
            ex.printStackTrace()
            false
        }
    }

    suspend fun updateInfo(userInfo: UserInfo): Boolean {
        return try {
            IrzClient.usersApi.updateInfo(userInfo).isSuccessful
        } catch (ex: Throwable) {
            ex.printStackTrace()
            false
        }
    }

    suspend fun getUsers(searchParams: SearchParams): OpResult<List<UserShort>> {
        return try {
            val response = IrzClient.usersApi.getUsers(
                searchParams.positionId,
                searchParams.role,
                searchParams.searchString,
                searchParams.pageIndex,
                searchParams.pageSize
            )
            if (!response.isSuccessful) {
                return OpResult.Failure()
            }
            val list = response.body()!!.map {
                UserShort(
                    it.id,
                    it.firstName,
                    it.surname,
                    it.patronymic.orEmpty(),
                    getImage(it.imageId)
                )
            }.toList()
            OpResult.Success(list)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            OpResult.Failure()
        }
    }

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
}