package ru.avtomaton.irz.app.activity.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.avtomaton.irz.app.activity.AppCompatActivityBase
import ru.avtomaton.irz.app.activity.ChatActivity
import ru.avtomaton.irz.app.client.IrzHttpClient.loadImageBy
import ru.avtomaton.irz.app.databinding.MessengerItemBinding
import ru.avtomaton.irz.app.model.pojo.Chat
import ru.avtomaton.irz.app.model.repository.MessengerRepository
import java.util.*

/**
 * @author Anton Akkuzin
 */
class MessengerAdapter(
    private val context: AppCompatActivityBase,
    private val userId: UUID
) : RecyclerView.Adapter<MessengerAdapter.ChatViewHolder>() {

    private val chatsList: MutableList<Chat> = mutableListOf()
    private val pageSize = 20

    init {
        context.async { updateChats(0) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = MessengerItemBinding.inflate(from, parent, false)
        return ChatViewHolder(binding)
    }

    override fun getItemCount(): Int = chatsList.size

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatsList[position])
        holder.itemView.setOnClickListener {
            val intent = ChatActivity.open(
                context,
                holder.chatId,
                userId,
                holder.recipientId
            )
            context.startActivity(intent)
        }
        onChatsUpdate(position)
    }

    private fun onChatsUpdate(position: Int) {
        if (itemCount % pageSize != 0 || itemCount - position != 5) {
            return
        }
        context.async { updateChats(itemCount / pageSize) }
    }

    private suspend fun updateChats(pageIndex: Int) {
        val result = MessengerRepository.getChats(pageIndex, pageSize)
        if (result.isFailure) {
            context.error()
            return
        }
        result.value()
            .filter { it.lastMessage != null }
            .forEach {
                chatsList.add(it)
                notifyItemInserted(itemCount - 1)
            }
    }

    inner class ChatViewHolder(
        private val chatItem: MessengerItemBinding
    ) : RecyclerView.ViewHolder(chatItem.root) {

        lateinit var chatId: UUID
        lateinit var recipientId: UUID

        fun bind(chat: Chat) {
            chatItem.apply {
                val user = chat.recipient
                user.imageId?.also { Glide.with(context).loadImageBy(it).centerCrop().into(userImage) }
                userName.text = user.fullName
                chat.lastMessage?.let {
                    val text = "${if (it.senderId == userId) "Вы: " else ""}${it.text}"
                    lastMessage.text = text
                    date.text = AppCompatActivityBase.dateFormat.format(it.date)
                }
            }
            chatId = chat.id
            recipientId = chat.recipient.id
        }
    }
}