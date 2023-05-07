package ru.avtomaton.irz.app.activity.util

import android.app.AlertDialog
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.microsoft.signalr.HubConnection
import ru.avtomaton.irz.app.activity.AppCompatActivityBase
import ru.avtomaton.irz.app.client.IrzSignalRClientBuilder
import ru.avtomaton.irz.app.databinding.MessageItemBinding
import ru.avtomaton.irz.app.model.pojo.Message
import ru.avtomaton.irz.app.model.pojo.MessageDto
import ru.avtomaton.irz.app.model.repository.ImageRepository
import ru.avtomaton.irz.app.model.repository.MessengerRepository
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
class ChatAdapter(
    private val context: AppCompatActivityBase,
    private val recycler: RecyclerView,
    private val chatId: UUID,
    private val recipientId: UUID,
    private val ownerId: UUID,
    private val searchString: String? = null
) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    private var connection: HubConnection? = null // IrzSignalRClientBuilder.build()
    private val messagesList: MutableList<Message> = mutableListOf()
    private val pageSize = 20

    constructor(ins: ChatAdapter, searchString: String?) : this(
        ins.context,
        ins.recycler,
        ins.chatId,
        ins.recipientId,
        ins.ownerId,
        searchString
    ) {
        connection = ins.connection
    }

    init {
        if (connection == null) {
            connection = IrzSignalRClientBuilder.build()
            context.async {
                updateChats(0)
                connection!!.start().blockingAwait()
                connection!!.on(
                    "messageReceived",
                    { dto -> context.async { onMessageReceived(dto) } },
                    MessageDto::class.java
                )
                connection!!.on(
                    "messageDeleted",
                    { id -> onMessageDeleted(id) },
                    UUID::class.java
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = MessageItemBinding.inflate(from, parent, false)
        return MessageViewHolder(binding)
    }

    override fun getItemCount(): Int = messagesList.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messagesList[position])
        onMessagesUpdate(position)
    }

    fun send(text: String?, image: Bitmap?) {
        context.async {
            if (!MessengerRepository.postMessage(recipientId, text, image)) {
                context.error()
            }
        }
    }

    private suspend fun onMessageReceived(dto: MessageDto) {
        val image = dto.imageId?.let {
            val result = ImageRepository.getImage(it)
            if (result.isFailure)
                return
            result.value()
        }
        messagesList.add(0, Message(dto.id, dto.text ?: "", image, dto.date, dto.senderId))
        notifyItemInserted(0)
        recycler.scrollToPosition(0)
    }

    private fun onMessageDeleted(id: UUID) {
        val index = messagesList.indexOfFirst { it.id == id }
        if (index == -1) {
            return
        }
        messagesList.removeAt(index)
        context.runOnUiThread { notifyItemRemoved(index) }
    }

    private fun onMessagesUpdate(position: Int) {
        if (itemCount % pageSize != 0 || itemCount - position != 5) {
            return
        }
        context.async { updateChats(itemCount / pageSize) }
    }

    private suspend fun updateChats(pageIndex: Int) {
        val result = MessengerRepository.getMessages(chatId, pageIndex, pageSize, searchString)
        if (result.isFailure) {
            context.error()
            return
        }
        result.value().forEach {
            messagesList.add(it)
            notifyItemInserted(itemCount - 1)
        }
    }


    inner class MessageViewHolder(
        private val messageItem: MessageItemBinding
    ) : RecyclerView.ViewHolder(messageItem.root) {

        private lateinit var message: Message

        init {
            itemView.setOnLongClickListener {
                if (message.senderId != ownerId) {
                    return@setOnLongClickListener false
                }
                AlertDialog.Builder(context)
                    .setMessage("Удалить сообщение?")
                    .setPositiveButton("Да") { _, _ ->
                        context.async {
                            if (!MessengerRepository.deleteMessage(message.id)) {
                                context.error()
                            }
                        }
                    }
                    .setNegativeButton("Нет", null)
                    .show()
                return@setOnLongClickListener true
            }
        }

        fun bind(message: Message) {
            messageItem.apply {
                image.setImageBitmap(message.image)
                if (message.image == null)
                    image.visibility = View.GONE
                text.text = message.text
                date.text = AppCompatActivityBase.dateFormat.format(message.date)

                val params = card.layoutParams as ConstraintLayout.LayoutParams
                if (message.senderId != ownerId) {
                    params.startToStart = 0
                    params.endToEnd = -1
                } else {
                    params.startToStart = -1
                    params.endToEnd = 0
                }
            }
            this.message = message
        }
    }

    fun close() {
        connection!!.close()
    }
}
