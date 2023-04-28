package ru.avtomaton.irz.app.activity.util

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.avtomaton.irz.app.databinding.SearchUserBinding
import ru.avtomaton.irz.app.model.pojo.UserShort

/**
 * @author Anton Akkuzin
 */
class SearchUsersAdapter(private val listener: Listener) :
    RecyclerView.Adapter<SearchUsersAdapter.UserHolder>() {

    private val users: MutableList<UserShort> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = SearchUserBinding.inflate(from, parent, false)
        return UserHolder(binding)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        holder.bindUser(users[position])
        holder.itemView.setOnClickListener { listener.onUserClick(holder.getUser()) }
        listener.onUsersUpdate(itemCount, position)
    }

    fun updateUsers(users: List<UserShort>) {
        users.forEach {
            this.users.add(it)
            notifyItemInserted(this.users.size -1)
        }
    }

    inner class UserHolder(private val searchUserBinding: SearchUserBinding) :
        RecyclerView.ViewHolder(searchUserBinding.root) {

        private lateinit var user: UserShort

        fun getUser(): UserShort {
            return user
        }

        fun bindUser(user: UserShort) {
            this.user = user

            @SuppressLint("SetTextI18n")
            searchUserBinding.userName.text = "${user.surname} ${user.firstName} ${user.patronymic}"
            user.image?.also { searchUserBinding.userImage.setImageBitmap(it) }
        }
    }


    interface Listener {
        fun onUserClick(user: UserShort)

        fun onUsersUpdate(listSize: Int, position: Int)
    }
}