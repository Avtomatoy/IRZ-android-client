package ru.avtomaton.irz.app.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.avtomaton.irz.app.activity.util.ChatAdapter
import ru.avtomaton.irz.app.activity.util.DoNothingBackPressedCallback
import ru.avtomaton.irz.app.client.IrzHttpClient.loadImageBy
import ru.avtomaton.irz.app.databinding.ActivityChatBinding
import ru.avtomaton.irz.app.model.repository.UserRepository
import java.util.*

/**
 * @author Anton Akkuzin
 */
class ChatActivity : AppCompatActivityBase() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chatId = intent.getStringExtra(chatKey)!!.let { UUID.fromString(it) }
        val ownerId = intent.getStringExtra(ownerKey)!!.let { UUID.fromString(it) }
        val recipientId = intent.getStringExtra(recipientKey)!!.let { UUID.fromString(it) }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(MessengerActivity.open(this@ChatActivity))
            }

        })
        binding = ActivityChatBinding.inflate(layoutInflater).apply {
            async { bindRecipient(recipientId) }
            this@ChatActivity.adapter =
                ChatAdapter(this@ChatActivity, chatFeed, chatId, recipientId, ownerId)
            sendButton.setOnClickListener { send() }
            searchButton.setOnClickListener { search() }
            uploadImageButton.setOnClickListener { contract.launch("image/*") }
            onImageUploaded = {
                apply {
                    removeImageButton.visibility = View.VISIBLE
                    uploadImageButton.visibility = View.GONE
                }
            }
            removeImageButton.setOnClickListener { removeImage() }
            chatFeed.setUp()
            setContentView(root)
        }
    }

    private fun recreateAdapter(searchString: String?) {
        adapter = ChatAdapter(adapter, searchString)
        binding.chatFeed.setUp()
    }

    private fun RecyclerView.setUp() {
        layoutManager = LinearLayoutManager(
            this@ChatActivity,
            RecyclerView.VERTICAL,
            true
        )
        adapter = this@ChatActivity.adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.close()
    }

    private fun send() {
        binding.input.text.toString().apply {
            if (isBlank() && imageUri == null) return@apply
            adapter.send(this, imageUri?.toImageBytes())
            removeImage()
            binding.input.text = SpannableStringBuilder("")
            imageUri = null
        }
    }

    private fun search() {
        val input = binding.searchInput.text.toString()
        val searchString: String? = input.ifBlank { null }
        recreateAdapter(searchString)
        binding.searchInput.text = null
    }

    private fun removeImage() {
        binding.removeImageButton.visibility = View.GONE
        binding.uploadImageButton.visibility = View.VISIBLE
        imageUri = null
    }

    private suspend fun ActivityChatBinding.bindRecipient(recipientId: UUID) =
        UserRepository.getUser(recipientId).letIfSuccess {
            imageId?.also {
                Glide.with(this@ChatActivity).loadImageBy(it).centerCrop().into(userImage)
            }
            this@bindRecipient.userName.text = this.fullName
        }

    companion object {

        private const val chatKey: String = "chat"
        private const val ownerKey: String = "owner"
        private const val recipientKey: String = "recipient"

        fun open(context: Context, chatId: UUID, ownerId: UUID, recipientId: UUID): Intent {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(chatKey, chatId.toString())
            intent.putExtra(ownerKey, ownerId.toString())
            intent.putExtra(recipientKey, recipientId.toString())
            return intent
        }
    }
}