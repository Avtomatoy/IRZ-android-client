package ru.avtomaton.irz.app.activity.util

import java.util.*

/**
 * @author Anton Akkuzin
 */
data class NewsSearchParams(
    val authorId: UUID?,
    val publicOnly: Boolean,
    val likedOnly: Boolean,
    val searchString: String?,
    val pageIndex: Int,
    val pageSize: Int
) {
    class Builder {

        var authorId: UUID? = null
        var publicOnly: Boolean = false
        var likedOnly: Boolean = false
        var searchString: String? = null
        var pageIndex: Int = 0
        var pageSize: Int = 0

        fun build(): NewsSearchParams {
            return NewsSearchParams(
                authorId,
                publicOnly,
                likedOnly,
                searchString,
                pageIndex,
                pageSize
            )
        }

    }
}