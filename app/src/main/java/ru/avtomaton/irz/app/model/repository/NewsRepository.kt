package ru.avtomaton.irz.app.model.repository

import android.graphics.Bitmap
import retrofit2.Response
import ru.avtomaton.irz.app.client.IrzHttpClient
import ru.avtomaton.irz.app.model.OpResult
import ru.avtomaton.irz.app.model.pojo.UserShort
import ru.avtomaton.irz.app.model.pojo.News
import ru.avtomaton.irz.app.model.pojo.NewsDto
import ru.avtomaton.irz.app.model.pojo.User
import java.util.UUID
import java.util.function.Predicate

/**
 * @author Anton Akkuzin
 */
object NewsRepository {

    suspend fun tryLikeNews(newsId: UUID): Boolean {
        return try {
            IrzHttpClient.likesApi.like(newsId).isSuccessful
        } catch (ex: Throwable) {
            ex.printStackTrace()
            false
        }
    }

    suspend fun tryDislikeNews(newsId: UUID): Boolean {
        return try {
            IrzHttpClient.likesApi.dislike(newsId).isSuccessful
        } catch (ex: Throwable) {
            ex.printStackTrace()
            false
        }
    }

    suspend fun tryDeleteNews(newsId: UUID): Boolean {
        return try {
            val response = IrzHttpClient.newsApi.deleteNews(newsId)
            response.isSuccessful
        } catch (ex: Throwable) {
            ex.printStackTrace()
            false
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

    suspend fun getAuthorNews(
        userId: UUID,
        pageIndex: Int,
        pageSize: Int
    ): OpResult<List<News>> {
        return getAnyNews { IrzHttpClient.newsApi.getNews(userId, pageIndex, pageSize) }
    }

    suspend fun getNews(
        pageIndex: Int,
        pageSize: Int
    ): OpResult<List<News>> {
        return getAnyNews { IrzHttpClient.newsApi.getNews(null, pageIndex, pageSize) }
    }

    private suspend fun getAnyNews(block: suspend () -> Response<List<NewsDto>>)
            : OpResult<List<News>> {
        return try {
            val newsResponse = block()
            val newsObserver = UserRepository.getMe()
            if (!newsResponse.isSuccessful) {
                return OpResult.Failure()
            }
            val newsList = newsResponse.body()!!
                .map { it -> mapNewsDto(it) { canDelete(it, newsObserver) } }
                .toList()
            OpResult.Success(newsList)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            OpResult.Failure()
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
                authorImage
            ),
            dto.commentCount,
            canDelete
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