package ru.avtomaton.irz.app.model.repository

import retrofit2.Response
import ru.avtomaton.irz.app.activity.util.UserSearchParams
import ru.avtomaton.irz.app.client.IrzHttpClient
import ru.avtomaton.irz.app.model.OpResult
import ru.avtomaton.irz.app.model.pojo.*
import java.util.*

/**
 * @author Anton Akkuzin
 */
object UserRepository : Repository() {

    suspend fun updatePhoto(image: ByteArray): Boolean {
        return tryForSuccess {
            IrzHttpClient.usersApi.updatePhoto(image.asMultipartBody("file")).isSuccessful
        }
    }

    suspend fun deletePhoto(): Boolean {
        return tryForSuccess {
            IrzHttpClient.usersApi.deletePhoto().isSuccessful
        }
    }

    suspend fun updateInfo(userInfo: UserInfo): Boolean {
        return tryForSuccess {
            IrzHttpClient.usersApi.updateInfo(userInfo).isSuccessful
        }
    }

    suspend fun getUsers(userSearchParams: UserSearchParams): OpResult<List<UserShort>> {
        return tryForResult {
            IrzHttpClient.usersApi.getUsers(
                userSearchParams.positionId,
                userSearchParams.role,
                userSearchParams.searchString,
                userSearchParams.pageIndex,
                userSearchParams.pageSize
            ).letIfSuccess {
                this.body()!!.map { convert(it) }.toList()
            }
        }
    }

    suspend fun getMe(): OpResult<User> {
        return getAnyUser(
            { IrzHttpClient.usersApi.getMe() },
            { UserPositionsRepository.getMyPositions() }
        )
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
        return tryForResult {
            val positionsResult = positionsBlock.invoke()
            if (positionsResult.isFailure) {
                return@tryForResult OpResult.Failure()
            }
            userBlock().letIfSuccess { convert(this.body()!!, positionsResult.value()) }
        }
    }

    private suspend fun convert(dto: UserDto, positions: List<Position>): User {
        val isMe = getMyId()?.equals(dto.id) ?: false
        return User(
            dto.id,
            dto.firstName,
            dto.surname,
            dto.patronymic.orEmpty(),
            getFullName(dto),
            dto.birthday,
            dto.imageId,
            dto.aboutMyself.orDots(),
            dto.myDoings.orDots(),
            dto.skills.orDots(),
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

    private fun convert(dto: UserDto): UserShort {
        return UserShort(
            dto.id,
            dto.firstName,
            dto.surname,
            dto.patronymic.orEmpty(),
            getFullName(dto),
            dto.imageId
        )
    }

    private fun getFullName(dto: UserDto): String = listOf(
        dto.surname, dto.firstName, dto.patronymic.orEmpty()
    ).joinToString(separator = " ")

    private fun String?.orDots(): String = this ?: "…"

    private suspend fun getMyId(): UUID? {
        return try {
            IrzHttpClient.usersApi.getMe().let {
                if (it.isSuccessful) it.body()!!.id else null
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
            null
        }
    }
}
