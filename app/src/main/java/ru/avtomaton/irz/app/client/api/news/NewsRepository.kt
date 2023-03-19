package ru.avtomaton.irz.app.client.api.news

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.client.api.news.models.Author
import ru.avtomaton.irz.app.client.api.news.models.News
import ru.avtomaton.irz.app.client.api.news.models.NewsDto
import java.util.Base64
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
class NewsRepository {

    private val decoder: Base64.Decoder = Base64.getDecoder()

    suspend fun getNews(pageIndex: Int, pageSize: Int): MutableList<News>? {
        val response = IrzClient.newsApi.getNews(pageIndex, pageSize)
        if (!response.isSuccessful) {
            return null
        }
        val result = mutableListOf<News>()
        response.body()!!.forEach {
            val newsImage: Bitmap? =
                if (it.imageId == null) null else getImage(it.imageId)
            val authorImage: Bitmap? =
                if (it.authorDto.imageId == null) null else getImage(it.authorDto.imageId)
            result.add(convertDto(it, newsImage, authorImage))
        }
        return result
    }

    private suspend fun getImage(imageId: UUID): Bitmap? {
        val imageResponse = IrzClient.imagesApi.getImage(imageId)
        if (!imageResponse.isSuccessful) {
            return null
        }
        val data = imageResponse.body()!!.data
        try {
            return convertBase64Bytes(data)
        } catch (ignore: Throwable) {
        }

        return null
    }

    private fun convertBase64Bytes(base64Str: String): Bitmap {
        val bytes = decoder.decode(base64Str)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
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