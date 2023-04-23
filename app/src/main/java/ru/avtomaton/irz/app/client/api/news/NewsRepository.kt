package ru.avtomaton.irz.app.client.api.news

import android.graphics.Bitmap
import okhttp3.ResponseBody
import retrofit2.Response
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.client.Repository
import ru.avtomaton.irz.app.client.api.images.ImageRepository
import ru.avtomaton.irz.app.client.api.news.models.Author
import ru.avtomaton.irz.app.client.api.news.models.News
import ru.avtomaton.irz.app.client.api.news.models.NewsDto
import ru.avtomaton.irz.app.client.api.users.models.UserRoles
import ru.avtomaton.irz.app.infra.UserManager
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
class NewsRepository: Repository() {

    private val imageRepository: ImageRepository = ImageRepository()

    private suspend fun getNewsFullText(id: UUID): String {
        val response: Response<ResponseBody>
        try {
            response = authWrap(IrzClient.newsApi.getNewsFullText(id))
        } catch (ex: Throwable) {
            ex.printStackTrace()
            return ""
        }
        if (!response.isSuccessful) {
            return ""
        }
        return response.body()!!.string()
    }

    suspend fun getNewsWithFullText(news: News): News {
        val text = getNewsFullText(news.id)
        return News(
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
    }

    suspend fun getNews(pageIndex: Int, pageSize: Int): MutableList<News>? {
        val response: Response<List<NewsDto>>
        try {
            response = IrzClient.newsApi.getNews(pageIndex, pageSize)
        } catch (ex: Throwable) {
            return null
        }
        if (!response.isSuccessful) {
            return null
        }
        val result = mutableListOf<News>()
        response.body()!!.forEach {
            val newsImage: Bitmap? = if (it.imageId == null)
                null
            else imageRepository.getImage(it.imageId)

            val authorImage: Bitmap? = if (it.authorDto.imageId == null)
                null
            else imageRepository.getImage(it.authorDto.imageId)

            result.add(convertDto(it, newsImage, authorImage))
        }
        return result
    }


    private fun convertDto(dto: NewsDto, newsImage: Bitmap?, authorImage: Bitmap?): News {
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
            canDelete(dto)
        )
    }

    private fun canDelete(newsDto: NewsDto): Boolean {
        val user = UserManager.getInfo() ?: return false
        if (newsDto.isPublic && UserRoles.isSupport(user)) {
            return true
        }
        if (user.id == newsDto.authorDto.id) {
            return true
        }
        return false
    }
}