package ru.avtomaton.irz.app.activity.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.avtomaton.irz.app.activity.AppCompatActivityBase
import ru.avtomaton.irz.app.activity.ProfileActivity
import ru.avtomaton.irz.app.client.IrzHttpClient.loadImageBy
import ru.avtomaton.irz.app.databinding.CommentItemBinding
import ru.avtomaton.irz.app.model.pojo.Comment
import ru.avtomaton.irz.app.model.pojo.CommentToSend
import ru.avtomaton.irz.app.model.repository.NewsRepository
import java.util.*

/**
 * @author Anton Akkuzin
 */
class CommentsAdapter(
    private val context: AppCompatActivityBase,
    private val newsId: UUID,
) : RecyclerView.Adapter<CommentsAdapter.CommentHolder>() {

    private val commentsList: MutableList<Comment> = mutableListOf()
    private val pageSize = 10

    init {
        context.async { updateComments(0) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CommentItemBinding.inflate(from, parent, false)
        return CommentHolder(binding)
    }

    override fun getItemCount(): Int = commentsList.size

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        holder.bind(commentsList[position])
        context.async { onCommentsUpdate(position) }
    }

    fun post(comment: CommentToSend) {
        context.async {
            val result = NewsRepository.postComment(comment)
            if (result.isFailure) {
                context.error()
                return@async
            }
            commentsList.add(result.value())
            context.runOnUiThread { notifyItemInserted(itemCount - 1) }
        }
    }

    private fun onCommentsUpdate(position: Int) {
        if (itemCount % pageSize != 0 || itemCount - position != 5)
            return
        context.async { updateComments(itemCount / pageSize) }
    }

    private suspend fun updateComments(position: Int) {
        val result = NewsRepository.getComments(newsId, position, pageSize)
        if (result.isFailure) {
            context.error()
            return
        }
        result.value().forEach {
            commentsList.add(it)
            context.runOnUiThread { notifyItemInserted(itemCount - 1) }
        }
    }

    inner class CommentHolder(
        private val binding: CommentItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var comment: Comment

        fun bind(comment: Comment) {
            this.comment = comment
            binding.apply {
                comment.user.imageId?.also { Glide.with(context).loadImageBy(it).into(image) }
                name.text = comment.user.fullName
                if (!comment.canDelete) deleteButton.visibility = View.GONE
                datetime.text = AppCompatActivityBase.dateFormat.format(comment.dateTime)
                text.text = comment.text
                deleteButton.setOnClickListener { context.async { delete() } }
                name.setOnClickListener {
                    val intent = ProfileActivity.openProfile(context, comment.user.id)
                    context.startActivity(intent)
                }
            }
        }

        private suspend fun delete() {
            if (NewsRepository.deleteComment(comment.id)) {
                val index = this@CommentsAdapter.commentsList.indexOf(comment)
                this@CommentsAdapter.commentsList.remove(comment)
                context.runOnUiThread { notifyItemRemoved(index) }
                return
            }
            context.error()
        }
    }
}