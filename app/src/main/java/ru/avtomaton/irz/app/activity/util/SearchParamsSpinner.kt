package ru.avtomaton.irz.app.activity.util

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import ru.avtomaton.irz.app.model.pojo.UserShort
import java.util.*

/**
 * @author Anton Akkuzin
 */
object SearchParamsSpinner {

    fun Spinner.positionSpinner(
        positionsMap: HashMap<String, UUID>,
        positionsList: List<String>,
        builder: UserSearchParams.Builder
    ) {
        mutableListOf("").let {
            it.addAll(positionsList)
            this.adapter = createAdapter(context, it)
            onItemSelectedListener = createListener { position ->
                builder.positionId = positionsMap[it[position]]
            }
        }
    }

    fun Spinner.rolesSpinner(roles: List<String>, builder: UserSearchParams.Builder) {
        mutableListOf("").let {
            it.addAll(roles)
            this.adapter = createAdapter(context, it)
            onItemSelectedListener = createListener { position ->
                builder.role = if (position == 0) null else it[position]
            }
        }
    }

    fun Spinner.userSpinner(users: List<UserShort>, builder: NewsSearchParams.Builder) {
        val map = HashMap<String, UUID>()
        users.forEach { map[it.fullName] = it.id }
        mutableListOf("").let { list ->
            list.addAll(users.sortedBy { it.fullName }.map { it.fullName })
            this.adapter = createAdapter(context, list)
            onItemSelectedListener = createListener { position ->
                builder.authorId = map[list[position]]
            }
        }
    }

    private fun createAdapter(context: Context, values: MutableList<String>): ArrayAdapter<String> {
        return ArrayAdapter(context, android.R.layout.simple_spinner_item, values).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun createListener(block: (Int) -> Unit): OnItemSelectedListener {
        return object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) = block(position)

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}
