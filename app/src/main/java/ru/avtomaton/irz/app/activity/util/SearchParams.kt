package ru.avtomaton.irz.app.activity.util

import java.util.UUID

/**
 * @author Anton Akkuzin
 */
class SearchParams(
    val searchString: String?,
    val positionId: UUID?,
    val role: String?,
    val pageIndex: Int,
    val pageSize: Int,
) {

    class Builder {

        private var searchString: String? = null
        private var positionId: UUID? = null
        private var role: String? = null
        private var pageIndex: Int = 0
        private var pageSize: Int = 0

        fun searchString(searchString: String?): Builder {
            this.searchString = searchString
            return this
        }

        fun positionId(positionId: UUID?): Builder {
            this.positionId = positionId
            return this
        }

        fun role(role: String?): Builder {
            this.role = role
            return this
        }

        fun pageIndex(pageIndex: Int): Builder {
            this.pageIndex = pageIndex
            return this
        }

        fun pageSize(pageSize: Int): Builder {
            this.pageSize = pageSize
            return this
        }

        fun build(): SearchParams {
            return SearchParams(
                this.searchString,
                this.positionId,
                this.role,
                this.pageIndex,
                this.pageSize
            )
        }
    }
}