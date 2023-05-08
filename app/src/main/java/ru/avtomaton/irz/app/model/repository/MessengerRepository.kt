package ru.avtomaton.irz.app.model.repository

import android.graphics.Bitmap
import ru.avtomaton.irz.app.client.IrzHttpClient
import ru.avtomaton.irz.app.model.OpResult
import ru.avtomaton.irz.app.model.pojo.*
import ru.avtomaton.irz.app.services.Base64Converter
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
object MessengerRepository : Repository() {

    suspend fun postMessage(recipientId: UUID, text: String?, image: Bitmap?): Boolean {
        val imageDto = image?.let {
            val result = Base64Converter.convert(it)
            if (result.isFailure) {
                return false
            }
            ImageDto("final", "png", result.value())
        }
        return tryForSuccess {
            IrzHttpClient.messengerApi.postMessage(MessageToSend(recipientId, text, imageDto))
                .isSuccessful
        }
    }

    suspend fun deleteMessage(messageId: UUID): Boolean {
        return tryForSuccess {
            IrzHttpClient.messengerApi.deleteMessage(messageId).isSuccessful
        }
    }

    suspend fun getMessages(
        chatId: UUID,
        pageIndex: Int,
        pageSize: Int,
        searchString: String? = null
    ): OpResult<List<Message>> {
        return tryForResult {
            val response =
                IrzHttpClient.messengerApi.getMessages(chatId, pageIndex, pageSize, searchString)
            if (!response.isSuccessful) {
                return@tryForResult OpResult.Failure()
            }
            val list = response.body()!!.map { convert(it) }.toList()
            OpResult.Success(list)
        }
    }

    suspend fun getChats(pageIndex: Int, pageSize: Int): OpResult<List<Chat>> {
        return tryForResult {
            val response = IrzHttpClient.messengerApi.getChats(pageIndex, pageSize)
            if (!response.isSuccessful) {
                return@tryForResult OpResult.Failure()
            }
            val list = response.body()!!.map { convert(it) }.toList()
            OpResult.Success(list)
        }
    }

    suspend fun getChatFor(userId: UUID): OpResult<UUID> {
        return tryForResult {
            val response = IrzHttpClient.messengerApi.getChatForUser(userId)
            if (!response.isSuccessful) {
                return@tryForResult OpResult.Failure()
            }
            OpResult.Success(response.body()!!)
        }
    }
    private suspend fun convert(dto: ChatDto): Chat =
        Chat(
            dto.id,
            convert(dto.recipient),
            dto.lastMessage?.let { convert(it) },
            dto.unreadedCount
        )

    private suspend fun convert(dto: MessageDto): Message =
        Message(
            dto.id,
            dto.text ?: "",
            getImage(dto.imageId),
            dto.date,
            dto.senderId
        )

    private suspend fun convert(dto: UserShortDto): UserShort =
        UserShort(
            dto.id,
            dto.firstName,
            dto.surname,
            dto.patronymic,
            "${dto.surname} ${dto.firstName} ${dto.patronymic}}",
            getImage(dto.imageId)
        )

    private suspend fun getImage(id: UUID?): Bitmap? {
        if (id == null) {
            return null
        }
        val image = ImageRepository.getImage(id)
        if (image.isFailure) {
            return null
        }
        return image.value()
    }
}