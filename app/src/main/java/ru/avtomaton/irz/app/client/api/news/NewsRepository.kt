package ru.avtomaton.irz.app.client.api.news

import android.graphics.Bitmap
import retrofit2.Response
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.client.api.images.ImageRepository
import ru.avtomaton.irz.app.client.api.news.models.Author
import ru.avtomaton.irz.app.client.api.news.models.News
import ru.avtomaton.irz.app.client.api.news.models.NewsDto

/**
 * @author Anton Akkuzin
 */
class NewsRepository {

    private val imageRepository: ImageRepository = ImageRepository()

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
            "${dto.text}...",
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
            dto.commentCount
        )
    }
}