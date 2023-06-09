package ru.avtomaton.irz.app.model.repository

import okhttp3.RequestBody
import ru.avtomaton.irz.app.client.IrzHttpClient
import ru.avtomaton.irz.app.model.OpResult
import ru.avtomaton.irz.app.model.pojo.*
import java.util.*

/**
 * @author Anton Akkuzin
 */
object MessengerRepository : Repository() {

    suspend fun postMessage(recipientId: UUID, text: String?, image: ByteArray?): Boolean {
        val userIdBody = RequestBody.create(textPlainType, recipientId.toString())
        val textBody = text?.let { RequestBody.create(textPlainType, it) }
        val imageBodyPart = image?.asMultipartBody("Image")

        return tryForSuccess {
            IrzHttpClient.messengerApi.postMessage(userIdBody, textBody, imageBodyPart)
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
            IrzHttpClient.messengerApi.getMessages(chatId, pageIndex, pageSize, searchString)
                .letIfSuccess {
                    this.body()!!.map { convert(it) }.toList()
                }
        }
    }

    suspend fun getChats(pageIndex: Int, pageSize: Int): OpResult<List<Chat>> {
        return tryForResult {
            IrzHttpClient.messengerApi.getChats(pageIndex, pageSize).letIfSuccess {
                this.body()!!.map { convert(it) }.toList()
            }
        }
    }

    suspend fun getChatFor(userId: UUID): OpResult<UUID> {
        return tryForResult {
            IrzHttpClient.messengerApi.getChatForUser(userId).letIfSuccess { this.body()!! }
        }
    }
    private fun convert(dto: ChatDto): Chat =
        Chat(
            dto.id,
            convert(dto.recipient),
            dto.lastMessage?.let { convert(it) },
            dto.unreadedCount
        )

    private fun convert(dto: MessageDto): Message =
        Message(
            dto.id,
            dto.text.orEmpty(),
            dto.imageId,
            dto.date,
            dto.senderId
        )

    private fun convert(dto: UserShortDto): UserShort =
        UserShort(
            dto.id,
            dto.firstName,
            dto.surname,
            dto.patronymic,
            "${dto.surname} ${dto.firstName} ${dto.patronymic.orEmpty()}",
            dto.imageId
        )
}