package com.example.sportsclub.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.SparseBooleanArray
import android.widget.Button
import android.widget.CheckedTextView
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.SimpleExpandableListAdapter
import android.widget.CalendarView
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sportsclub.R
import com.example.sportsclub.database.ActividadConHorarios
import com.example.sportsclub.database.ActividadRepository
import java.text.SimpleDateFormat
import java.util.*

class ActividadesListaActivity : AppCompatActivity() {
    private val checkedItems = HashMap<String, SparseBooleanArray>()
    private lateinit var actividadRepository: ActividadRepository
    private var actividadesData: List<Pair<String, List<String>>> = emptyList()
    private var fullActividadList: List<ActividadConHorarios> = emptyList()
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actividades_lista)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        actividadRepository = ActividadRepository(this)

        setupUI()
        val searchInput = findViewById<EditText>(R.id.search_input)

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterActivities(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            loadActivitiesForDate(selectedDate)
        }
        loadActivitiesFromDatabase()
    }

    private fun setupUI() {
        val menuBack = findViewById<ImageView>(R.id.backMenu)
        menuBack.setOnClickListener {
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)
        }

        val siguienteButton = findViewById<Button>(R.id.siguienteButton)
        siguienteButton.setOnClickListener {
            val selectedActivities = getCheckedItems()
            val intent = Intent(this, ActividadesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun filterActivities(query: String) {

        val filteredList = fullActividadList.filter { actividad ->
            val matchesName = actividad.nombreActividad.contains(query, ignoreCase = true)
            val sdfHorario = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val sdfSelected = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val matchesDate = selectedDate == null || actividad.horarios.any { horario ->
                try {
                    val horarioDate = sdfHorario.parse(horario.trim())
                    val horarioFormatted = sdfSelected.format(horarioDate!!)
                    horarioFormatted == selectedDate
                } catch (e: Exception) {
                    false
                }
            }

            matchesName && matchesDate
        }

        actividadesData = filteredList.map {
            Pair(it.nombreActividad, it.horarios)
        }

        setupExpandableListView()
    }

    private fun loadActivitiesFromDatabase() {
        try {
            fullActividadList = actividadRepository.getActividadesConHorarios()

            if (fullActividadList.isEmpty()) {
                return
            }

            actividadesData = fullActividadList .map { actividadConHorario ->
                Pair(actividadConHorario.nombreActividad, actividadConHorario.horarios)
            }

            setupExpandableListView()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupExpandableListView() {
        val expandableListView = findViewById<ExpandableListView>(R.id.expandableListView)

        val groupList = actividadesData.map { it.first }
        val childMapping = actividadesData.associate { it.first to it.second }

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
                    val groupData = actividadesData.find { it.first == group }
                    val childItem = groupData?.second?.getOrNull(position) ?: ""

                    if (childItem.isNotEmpty()) {
                        result.add(Pair(group, childItem))
                    }
                }
            }
        }

        return result
    }

    private fun loadActivitiesForDate(date: String) {
        try {
            selectedDate = date
            fullActividadList = actividadRepository.getActividadesPorFecha(date) // <-- use DB filter
            filterActivities(findViewById<EditText>(R.id.search_input).text.toString()) // still allows text search
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}