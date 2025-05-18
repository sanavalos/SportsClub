package com.example.sportsclub

import android.content.Intent
import android.os.Bundle
import android.util.SparseBooleanArray
import android.widget.Button
import android.widget.CheckedTextView
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.SimpleExpandableListAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActividadesListaActivity : AppCompatActivity() {
    private val checkedItems = HashMap<String, SparseBooleanArray>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actividades_lista)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val menuBack = findViewById<ImageView>(R.id.backMenu)
        menuBack.setOnClickListener {
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)
        }

        val siguienteButton = findViewById<Button>(R.id.siguienteButton)
        siguienteButton.setOnClickListener {
            val intent = Intent(this, ActividadesActivity::class.java)
            startActivity(intent)
        }

        val expandableListView = findViewById<ExpandableListView>(R.id.expandableListView)

        val groupList = listOf("FÚTBOL", "VOLEY", "BÁSQUETBOL")
        val childMapping = mapOf(
            "FÚTBOL" to listOf("14/04/2025 08:00hs", "14/04/2025 17:00hs", "14/04/2025 21:00hs"),
            "VOLEY" to listOf("14/04/2025 09:00hs", "14/04/2025 18:00hs", "14/04/2025 22:00hs"),
            "BÁSQUETBOL" to listOf("14/04/2025 10:00hs", "14/04/2025 19:00hs")
        )

        groupList.forEach { group ->
            val itemCount = childMapping[group]?.size ?: 0
            checkedItems[group] = SparseBooleanArray(itemCount)
        }

        val groupData = groupList.map { mapOf("GROUP_NAME" to it) }
        val childData = groupList.map { group ->
            childMapping[group]?.map { mapOf("CHILD_NAME" to it) } ?: emptyList()
        }

        val adapter = SimpleExpandableListAdapter(
            this,
            groupData,
            android.R.layout.simple_expandable_list_item_1,
            arrayOf("GROUP_NAME"),
            intArrayOf(android.R.id.text1),
            childData,
            android.R.layout.simple_list_item_multiple_choice,
            arrayOf("CHILD_NAME"),
            intArrayOf(android.R.id.text1)
        )

        expandableListView.setAdapter(adapter)

        expandableListView.setOnChildClickListener { parent, view, groupPosition, childPosition, id ->
            val group = groupList[groupPosition]
            val checkedArray = checkedItems[group] ?: SparseBooleanArray()

            val isCurrentlyChecked = checkedArray.get(childPosition, false)
            checkedArray.put(childPosition, !isCurrentlyChecked)

            val checkedTextView = view.findViewById<CheckedTextView>(android.R.id.text1)
            checkedTextView.isChecked = !isCurrentlyChecked

            true
        }
    }

    private fun getCheckedItems(): List<Pair<String, String>> {
        val result = mutableListOf<Pair<String, String>>()

        checkedItems.forEach { (group, checkedArray) ->
            for (i in 0 until checkedArray.size()) {
                val position = checkedArray.keyAt(i)
                val isChecked = checkedArray.valueAt(i)

                if (isChecked) {
                    val childItem = when (group) {
                        "FÚTBOL" -> listOf("14/04/2025 08:00hs", "14/04/2025 17:00hs", "14/04/2025 21:00hs")[position]
                        "VOLEY" -> listOf("14/04/2025 09:00hs", "14/04/2025 18:00hs", "14/04/2025 22:00hs")[position]
                        "BÁSQUETBOL" -> listOf("14/04/2025 10:00hs", "14/04/2025 19:00hs")[position]
                        else -> ""
                    }

                    result.add(Pair(group, childItem))
                }
            }
        }

        return result
    }
}