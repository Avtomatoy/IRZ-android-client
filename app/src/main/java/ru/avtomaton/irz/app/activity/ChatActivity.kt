package ru.avtomaton.irz.app.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.avtomaton.irz.app.activity.util.ChatAdapter
import ru.avtomaton.irz.app.databinding.ActivityChatBinding
import ru.avtomaton.irz.app.model.repository.UserRepository
import java.util.*

/**
 * @author Anton Akkuzin
 */
class ChatActivity : AppCompatActivityBase() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: ChatAdapter

    private var image: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chatId = intent.getStringExtra(chatKey)!!.let { UUID.fromString(it) }
        val ownerId = intent.getStringExtra(ownerKey)!!.let { UUID.fromString(it) }
        val recipientId = intent.getStringExtra(recipientKey)!!.let { UUID.fromString(it) }

        binding = ActivityChatBinding.inflate(layoutInflater).apply {
            async { bindRecipient(recipientId) }
            this@ChatActivity.adapter =
                ChatAdapter(this@ChatActivity, chatFeed, chatId, recipientId, ownerId)
            sendButton.setOnClickListener { send() }
            searchButton.setOnClickListener { search() }
            uploadImageButton.setOnClickListener { uploadImage() }
            onImageUploaded = { uri -> uploadCallback(uri) }
            removeImageButton.setOnClickListener { removeImage() }
            chatFeed.setUp()
        }
        setContentView(binding.root)
    }

    private fun recreateAdapter(searchString: String?) {
        adapter = ChatAdapter(adapter, searchString)
        binding.chatFeed.setUp()
    }

    private fun RecyclerView.setUp() {
        layoutManager =
            LinearLayoutManager(this@ChatActivity, RecyclerView.VERTICAL, true)
        adapter = this@ChatActivity.adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.close()
    }

    private fun send() {
        binding.input.text.toString().apply {
            if (isBlank() && image == null) {
                return@apply
            }
            adapter.send(this, image)
            removeImage()
            binding.input.text = SpannableStringBuilder("")
            image = null
        }
    }

    private fun search() {
        val input = binding.searchInput.text.toString()
        val searchString: String? = input.ifBlank { null }
        recreateAdapter(searchString)
        binding.searchInput.text = null
    }

    private fun uploadCallback(uri: Uri) {
        @Suppress("DEPRECATION")
        image = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        binding.removeImageButton.visibility = View.VISIBLE
        binding.uploadImageButton.visibility = View.GONE
    }

    private fun removeImage() {
        binding.removeImageButton.visibility = View.GONE
        binding.uploadImageButton.visibility = View.VISIBLE
        image = null
    }

    private suspend fun ActivityChatBinding.bindRecipient(recipientId: UUID) =
        UserRepository.getUser(recipientId).letIfSuccess {
            image?.let { this@bindRecipient.userImage.setImageBitmap(it) }
            @SuppressLint("SetTextI18n")
            this@bindRecipient.userName.text =
                "${this.surname} ${this.firstName} ${this.patronymic}"
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