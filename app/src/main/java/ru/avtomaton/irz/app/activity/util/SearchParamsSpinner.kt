package ru.avtomaton.irz.app.activity.util

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
object SearchParamsSpinner {

    fun Spinner.positionSpinner(
        positionsMap: HashMap<String, UUID>,
        positionsList: List<String>,
        builder: SearchParams.Builder
    ) {
        val values = mutableListOf("").apply { addAll(positionsList) }
        this.adapter = createAdapter(context, values)
        onItemSelectedListener = createListener { position ->
            builder.positionId(positionsMap[values[position]])
        }
    }

    fun Spinner.rolesSpinner(roles: List<String>, builder: SearchParams.Builder) {
        val values = mutableListOf("").apply { addAll(roles) }
        this.adapter = createAdapter(context, values)
        onItemSelectedListener = createListener { position ->
            if (position == 0) builder.role(null) else builder.role(values[position])
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
