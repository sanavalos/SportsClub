package com.example.sportsclub.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
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
import com.example.sportsclub.database.ActividadRepository
import com.example.sportsclub.models.SelectedActividadData
import java.text.SimpleDateFormat
import java.util.*

class ActividadesListaActivity : AppCompatActivity() {
    data class ActividadUIItem(
        val actividadId: Int,
        val nombre: String,
        val fechaHora: String,
        val actividadProgramadaId: Int
    )

    private val selectedProgrammedIds = mutableSetOf<Int>()
    private lateinit var actividadRepository: ActividadRepository
    private var actividadesData: Map<String, List<ActividadUIItem>> = emptyMap()
    private var fullActividadList: List<ActividadUIItem> = emptyList()
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
        val toggleCalendarButton = findViewById<Button>(R.id.toggleCalendarButton)
        calendarView.visibility = View.GONE

        toggleCalendarButton.setOnClickListener {
            calendarView.visibility = if (calendarView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            toggleCalendarButton.visibility = if (toggleCalendarButton.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            selectedDate = date
            calendarView.visibility = View.GONE
            toggleCalendarButton.visibility = View.VISIBLE
            loadActivitiesForDate(date)
        }

        loadActivitiesForDate(selectedDate ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }

    private fun setupUI() {
        findViewById<ImageView>(R.id.backMenu).setOnClickListener {
            startActivity(Intent(this, MainMenu::class.java))
        }

        findViewById<Button>(R.id.siguienteButton).setOnClickListener {
            val selected = getCheckedItems()
            val selectedData = getSelectedActividadesWithDetails(selected)
            val intent = Intent(this, ActividadesActivity::class.java)
            intent.putParcelableArrayListExtra("selected_activities", ArrayList(selectedData))
            startActivity(intent)
        }
    }

    private fun loadActivitiesForDate(date: String) {
        val rawList = actividadRepository.getActividadesPorFecha(date)
        val mapped = mutableListOf<ActividadUIItem>()

        rawList.forEach { actividad ->
            val actividadEntity = actividadRepository.getActividadByName(actividad.nombreActividad) ?: return@forEach
            actividad.horarios.forEach { horario ->
                val idProgramada = actividadRepository.getActividadProgramadaId(actividadEntity.idActividad, horario)
                mapped.add(
                    ActividadUIItem(
                        actividadId = actividadEntity.idActividad,
                        nombre = actividad.nombreActividad,
                        fechaHora = horario,
                        actividadProgramadaId = idProgramada
                    )
                )
            }
        }

        fullActividadList = (fullActividadList + mapped)
            .distinctBy { it.actividadProgramadaId }
        filterActivities(findViewById<EditText>(R.id.search_input).text.toString())
    }

    private fun filterActivities(query: String) {
        val filtered = fullActividadList.filter {
            it.nombre.contains(query, ignoreCase = true) &&
                    (selectedDate == null || it.fechaHora.startsWith(
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate!!)
                        )
                    ))
        }

        actividadesData = filtered.groupBy { it.nombre }
        setupExpandableListView()
    }

    private fun setupExpandableListView() {
        val expandableListView = findViewById<ExpandableListView>(R.id.expandableListView)
        val groupList = actividadesData.keys.toList()
        val childData = groupList.map { group ->
            actividadesData[group]?.map { mapOf("CHILD_NAME" to it.fechaHora) } ?: emptyList()
        }

        val adapter = SimpleExpandableListAdapter(
            this,
            groupList.map { mapOf("GROUP_NAME" to it) },
            android.R.layout.simple_expandable_list_item_1,
            arrayOf("GROUP_NAME"),
            intArrayOf(android.R.id.text1),
            childData,
            android.R.layout.simple_list_item_multiple_choice,
            arrayOf("CHILD_NAME"),
            intArrayOf(android.R.id.text1)
        )

        expandableListView.setAdapter(adapter)
        expandableListView.setOnChildClickListener { _, view, groupPos, childPos, _ ->
            val group = groupList[groupPos]
            val child = actividadesData[group]?.get(childPos)
            child?.let {
                if (selectedProgrammedIds.contains(it.actividadProgramadaId)) {
                    selectedProgrammedIds.remove(it.actividadProgramadaId)
                } else {
                    selectedProgrammedIds.add(it.actividadProgramadaId)
                }

                val checkView = view.findViewById<CheckedTextView>(android.R.id.text1)
                checkView.isChecked = selectedProgrammedIds.contains(it.actividadProgramadaId)
            }
            true
        }
    }

    private fun getCheckedItems(): List<ActividadUIItem> {
        return fullActividadList.filter { selectedProgrammedIds.contains(it.actividadProgramadaId) }
    }

    private fun getSelectedActividadesWithDetails(selectedItems: List<ActividadUIItem>): List<SelectedActividadData> {
        return selectedItems.map {
            val precio = actividadRepository.getActividadByName(it.nombre)?.precio ?: 0.0
            SelectedActividadData(
                idActividadProgramada = it.actividadProgramadaId,
                nombreActividad = it.nombre,
                precio = precio,
                fechaHora = it.fechaHora
            )
        }
    }
}