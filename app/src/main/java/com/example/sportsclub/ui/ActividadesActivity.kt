package com.example.sportsclub.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sportsclub.R
import com.example.sportsclub.models.SelectedActividadData
import java.text.NumberFormat
import java.util.Locale

class ActividadesActivity : AppCompatActivity() {
    private var selectedActivities: List<SelectedActividadData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actividades)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        selectedActivities = intent.getParcelableArrayListExtra(
            "selected_activities",
            SelectedActividadData::class.java
        )
        setupUI()
        displaySelectedActivities()
    }

    private fun setupUI() {
        val menuBack = findViewById<ImageView>(R.id.backMenu)
        menuBack.setOnClickListener {
            val intent = Intent(this, ActividadesListaActivity::class.java)
            startActivity(intent)
        }

        val siguienteButton = findViewById<Button>(R.id.siguienteButton)
        siguienteButton.setOnClickListener {
            val intent = Intent(this, DatosPagoActivity::class.java)
            val selectedIds = selectedActivities?.map { it.idActividadProgramada }?.toIntArray()
            intent.putExtra("selected_activity_ids", selectedIds)
            startActivity(intent)
        }
    }

    private fun displaySelectedActivities() {
        val activitiesContainer = findViewById<LinearLayout>(R.id.activitiesContainer)

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "AR"))

        activitiesContainer.removeAllViews()

        selectedActivities?.forEach { activity ->
            val activityView = layoutInflater.inflate(R.layout.item_selected_activity, activitiesContainer, false)

            val nameTextView = activityView.findViewById<TextView>(R.id.activityNameTextView)
            val scheduleTextView = activityView.findViewById<TextView>(R.id.activityScheduleTextView)
            val priceTextView = activityView.findViewById<TextView>(R.id.activityPriceTextView)

            nameTextView.text = activity.nombreActividad
            scheduleTextView.text = activity.fechaHora
            priceTextView.text = currencyFormat.format(activity.precio)

            activitiesContainer.addView(activityView)
        }
    }

}