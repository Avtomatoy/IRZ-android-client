package ru.avtomaton.irz.app.activity.util

import java.util.UUID

/**
 * @author Anton Akkuzin
 */
data class UserSearchParams(
    val searchString: String?,
    val positionId: UUID?,
    val role: String?,
    val pageIndex: Int,
    val pageSize: Int,
) {

    class Builder {

        var searchString: String? = null
        var positionId: UUID? = null
        var role: String? = null
        var pageIndex: Int = 0
        var pageSize: Int = 0

        fun build(): UserSearchParams {
            return UserSearchParams(
                searchString,
                positionId,
                role,
                pageIndex,
                pageSize
            )
        }
    }
}