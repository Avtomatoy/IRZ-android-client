package ru.avtomaton.irz.app.client.api.news

import android.graphics.Bitmap
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.client.OpResult
import ru.avtomaton.irz.app.client.Repository
import ru.avtomaton.irz.app.client.api.images.ImageRepository
import ru.avtomaton.irz.app.client.api.news.models.Author
import ru.avtomaton.irz.app.client.api.news.models.News
import ru.avtomaton.irz.app.client.api.news.models.NewsDto
import ru.avtomaton.irz.app.client.api.users.UserRepository
import ru.avtomaton.irz.app.client.api.users.models.User
import ru.avtomaton.irz.app.client.api.users.models.UserRoles
import java.util.UUID
import java.util.function.Predicate

/**
 * @author Anton Akkuzin
 */
object NewsRepository : Repository() {

    private suspend fun getNewsFullText(id: UUID): String? {
        return try {
            val response = IrzClient.newsApi.getNewsFullText(id)
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

    suspend fun getNews(pageIndex: Int, pageSize: Int): OpResult<List<News>> {
        return try {
            val newsResponse = IrzClient.newsApi.getNews(pageIndex, pageSize)
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
        newsImage: Bitmap?, authorImage: Bitmap?, canDelete: Boolean): News {
        return News(
            dto.id,
            dto.title,
            "${dto.text}${if (dto.isClipped) "..." else ""}",
            newsImage,
            dto.dateTime,
            dto.isLiked,
            dto.likesCount,
            Author(
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
        if (newsDto.isPublic && UserRoles.isSupport(user)) {
            return true
        }
        if (user.id == newsDto.authorDto.id) {
            return true
        }
        return false
    }


//    @Deprecated("")
//    suspend fun getNewsWithFullTextOld(news: News): News {
//        val text = getNewsFullText(news.id)
//        return News(
//            news.id,
//            news.title,
//            text ?: "",
//            news.image,
//            news.dateTime,
//            news.isLiked,
//            news.likesCount,
//            news.author,
//            news.commentCount,
//            news.canDelete
//        )
//    }
//
//    @Deprecated("")
//    suspend fun getNewsOld(pageIndex: Int, pageSize: Int): MutableList<News>? {
//        val response: Response<List<NewsDto>>
//        try {
//            response = IrzClient.newsApi.getNews(pageIndex, pageSize)
//        } catch (ex: Throwable) {
//            return null
//        }
//        if (!response.isSuccessful) {
//            return null
//        }
//        val result = mutableListOf<News>()
//        response.body()!!.forEach {
//            val newsImage: Bitmap? = if (it.imageId == null)
//                null
//            else ImageRepository.getImageOld(it.imageId)
//
//            val authorImage: Bitmap? = if (it.authorDto.imageId == null)
//                null
//            else ImageRepository.getImageOld(it.authorDto.imageId)
//
//            result.add(convertDto(it, newsImage, authorImage))
//        }
//        return result
//    }
}