package ru.avtomaton.irz.app.model.repository

import android.graphics.Bitmap
import retrofit2.Response
import ru.avtomaton.irz.app.activity.util.UserSearchParams
import ru.avtomaton.irz.app.client.IrzHttpClient
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
            val response = IrzHttpClient.usersApi.updatePhoto(imageDto)
            response.isSuccessful
        } catch (ex: Throwable) {
            ex.printStackTrace()
            false
        }
    }

    suspend fun deletePhoto(): Boolean {
        return try {
            IrzHttpClient.usersApi.deletePhoto().isSuccessful
        } catch (ex: Throwable) {
            ex.printStackTrace()
            false
        }
    }

    suspend fun updateInfo(userInfo: UserInfo): Boolean {
        return try {
            IrzHttpClient.usersApi.updateInfo(userInfo).isSuccessful
        } catch (ex: Throwable) {
            ex.printStackTrace()
            false
        }
    }

    suspend fun getUsers(userSearchParams: UserSearchParams): OpResult<List<UserShort>> {
        return try {
            val response = IrzHttpClient.usersApi.getUsers(
                userSearchParams.positionId,
                userSearchParams.role,
                userSearchParams.searchString,
                userSearchParams.pageIndex,
                userSearchParams.pageSize
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
            { IrzHttpClient.usersApi.getMe() },
            { UserPositionsRepository.getMyPositions() }
        )
        if (user.isOk) {
            me = user.value()
        }
        return user
    }

    suspend fun getUser(id: UUID): OpResult<User> {
        return getAnyUser(
            { IrzHttpClient.usersApi.user(id) },
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

    private suspend fun convert(dto: UserDto, positions: List<Position>): User {
        val isMe = getMyId()?.equals(dto.id) ?: false
        return User(
            dto.id,
            dto.firstName,
            dto.surname,
            dto.patronymic.orEmpty(),
            "${dto.surname} ${dto.firstName}${if (dto.patronymic == null) "" else " ${dto.patronymic}"}",
            dto.birthday,
            getImage(dto.imageId),
            dto.aboutMyself ?: threeDots,
            dto.myDoings ?: threeDots,
            dto.skills ?: threeDots,
            dto.subscribersCount,
            dto.subscriptionsCount,
            dto.isSubscription,
            dto.email,
            dto.isActiveAccount,
            dto.roles,
            positions,
            isMe
        )
    }

    private suspend fun getMyId(): UUID? {
        return try {
            val response = IrzHttpClient.usersApi.getMe()
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