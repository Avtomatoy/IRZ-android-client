package ru.avtomaton.irz.app.client.api

import retrofit2.Response
import retrofit2.http.*
import ru.avtomaton.irz.app.constants.MESSENGER
import ru.avtomaton.irz.app.model.pojo.ChatDto
import ru.avtomaton.irz.app.model.pojo.MessageDto
import ru.avtomaton.irz.app.model.pojo.MessageToSend
import java.util.*

/**
 * @author Anton Akkuzin
 */
interface MessengerApi {

    @GET("$MESSENGER/messages")
    suspend fun getMessages(
        @Query("ChatId") chatId: UUID,
        @Query("PageIndex") pageIndex: Int,
        @Query("PageSize") pageSize: Int,
        @Query("SearchString") searchString: String? = null
    ): Response<List<MessageDto>>

    @POST("$MESSENGER/messages")
    suspend fun postMessage(@Body messageToSend: MessageToSend): Response<Unit>

    @DELETE("$MESSENGER/messages/{id}")
    suspend fun deleteMessage(@Path("id") id: UUID): Response<Unit>


    @GET("$MESSENGER/chats")
    suspend fun getChats(
        @Query("PageIndex") pageIndex: Int,
        @Query("PageSize") pageSize: Int
    ): Response<List<ChatDto>>

    @GET("$MESSENGER/chats/by_participant")
    suspend fun getChatForUser(@Query("participantId") userId: UUID): Response<UUID>

}