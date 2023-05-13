package ru.avtomaton.irz.app.model.repository

import android.graphics.Bitmap
import ru.avtomaton.irz.app.activity.util.NewsSearchParams
import ru.avtomaton.irz.app.client.IrzHttpClient
import ru.avtomaton.irz.app.model.OpResult
import ru.avtomaton.irz.app.model.pojo.*
import java.util.*
import java.util.function.Predicate

/**
 * @author Anton Akkuzin
 */
object NewsRepository : Repository() {

    suspend fun getComments(id: UUID, pageIndex: Int, pageSize: Int): OpResult<List<Comment>> {
        return tryForResult {
            val me = UserRepository.getMe()
            if (me.isFailure) {
                return@tryForResult OpResult.Failure()
            }
            IrzHttpClient.newsApi.getComments(id, pageIndex, pageSize).letIfSuccess {
                this.body()!!.map { convert(it) { id -> me.value().id == id } }.toList()
            }
        }
    }

    suspend fun postComment(comment: CommentToSend): OpResult<Comment> {
        return tryForResult {
            IrzHttpClient.newsApi.postComment(comment).letIfSuccess {
                convert(this.body()!!) { true }
            }
        }
    }

    suspend fun deleteComment(id: UUID): Boolean {
        return tryForSuccess {
            IrzHttpClient.newsApi.deleteComment(id).isSuccessful
        }
    }

    suspend fun tryLikeNews(newsId: UUID): Boolean {
        return tryForSuccess {
            IrzHttpClient.likesApi.like(newsId).isSuccessful
        }
    }

    suspend fun tryDislikeNews(newsId: UUID): Boolean {
        return tryForSuccess {
            IrzHttpClient.likesApi.dislike(newsId).isSuccessful
        }
    }

    suspend fun tryDeleteNews(newsId: UUID): Boolean {
        return tryForSuccess {
            IrzHttpClient.newsApi.deleteNews(newsId).isSuccessful
        }
    }

    suspend fun postNews(newsBody: NewsBody): Boolean {
        return tryForSuccess {
            IrzHttpClient.newsApi.postNews(newsBody).isSuccessful
        }
    }

    private suspend fun getNewsFullText(id: UUID): String? {
        return try {
            val response = IrzHttpClient.newsApi.getNewsFullText(id)
            if (!response.isSuccessful) {
                return null
            }
            response.body()!!.string()
        } catch (ex: Throwable) {
            ex.printStackTrace()
            null
        }
    }

    suspend fun getNewsWithFulltext(news: News): OpResult<News> {
        val text = getNewsFullText(news.id) ?: return OpResult.Failure()
        val value = News(
            news.id,
            news.title,
            text,
            news.image,
            news.dateTime,
            news.isLiked,
            news.likesCount,
            news.author,
            news.commentCount,
            news.canDelete
        )
        return OpResult.Success(value)
    }

    suspend fun getNews(params: NewsSearchParams): OpResult<List<News>> {
        return tryForResult {
            IrzHttpClient.newsApi.getNews(
                params.authorId,
                params.publicOnly,
                params.likedOnly,
                params.pageIndex,
                params.pageSize
            ).letIfSuccess {
                val newsObserver = UserRepository.getMe()
                this.body()!!
                    .map { it -> mapNewsDto(it) { canDelete(it, newsObserver) } }
                    .toList()
            }
        }
    }

    private suspend fun mapNewsDto(newsDto: NewsDto, canDelete: Predicate<NewsDto>): News {
        return convertDto(
            newsDto,
            getImage(newsDto.imageId),
            getImage(newsDto.authorDto.imageId),
            canDelete.test(newsDto)
        )
    }

    private suspend fun getImage(imageId: UUID?): Bitmap? {
        if (imageId == null) {
            return null
        }
        val result = ImageRepository.getImage(imageId)
        return if (result.isOk) result.value() else null
    }

    private fun convertDto(
        dto: NewsDto,
        newsImage: Bitmap?, authorImage: Bitmap?, canDelete: Boolean
    ): News {
        val author = dto.authorDto
        return News(
            dto.id,
            dto.title,
            "${dto.text}${if (dto.isClipped) "..." else ""}",
            newsImage,
            dto.dateTime,
            dto.isLiked,
            dto.likesCount,
            UserShort(
                dto.authorDto.id,
                dto.authorDto.firstName,
                dto.authorDto.surname,
                dto.authorDto.patronymic,
                "${author.surname} ${author.firstName} ${author.patronymic}}",
                authorImage
            ),
            dto.commentCount,
            canDelete
        )
    }

    private suspend fun convert(dto: CommentDto, canDelete: Predicate<UUID>): Comment {
        val user = dto.user
        return Comment(
            dto.id,
            dto.text,
            dto.dateTime,
            UserShort(
                user.id,
                user.firstName,
                user.surname,
                user.patronymic,
                "${user.surname} ${user.firstName}",
                getImage(user.imageId)
            ),
            canDelete.test(user.id)
        )
    }

    private fun canDelete(newsDto: NewsDto, result: OpResult<User>): Boolean {
        if (result.isFailure) {
            return false
        }
        val user = result.value()
        if (newsDto.isPublic && user.isSupport()) {
            return true
        }
        if (user.id == newsDto.authorDto.id) {
            return true
        }
        return false
    }
}