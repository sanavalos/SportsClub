package com.example.sportsclub.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import com.example.sportsclub.database.UsuarioRepository
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sportsclub.R
import com.example.sportsclub.models.SelectedActividadData
import com.example.sportsclub.models.Usuario
import java.text.NumberFormat
import java.util.Locale

class ActividadesActivity : AppCompatActivity() {
    private var selectedActivities: List<SelectedActividadData>? = null
    private var usuarioSeleccionado: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actividades)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        @Suppress("DEPRECATION")
        selectedActivities = intent.getParcelableArrayListExtra<SelectedActividadData>("selected_activities")
        setupUI()
        displaySelectedActivities()
    }

    private fun setupUI() {
        val menuBack = findViewById<ImageView>(R.id.backMenu)
        val searchIcon = findViewById<ImageView>(R.id.search_icon)
        val searchInput = findViewById<EditText>(R.id.search_input)

        menuBack.setOnClickListener {
            startActivity(Intent(this, ActividadesListaActivity::class.java))
        }

        searchIcon.setOnClickListener {
            val documento = searchInput.text.toString().trim()
            if (documento.isNotEmpty()) {
                val usuarioRepository = UsuarioRepository(this)
                val usuario = usuarioRepository.buscarUsuarioPorDocumento(documento)

                if (usuario != null) {
                    mostrarUsuario(usuario)
                } else {
                    Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val siguienteButton = findViewById<Button>(R.id.siguienteButton)
        siguienteButton.setOnClickListener {
            val usuario = usuarioSeleccionado
            val actividades = selectedActivities

            if (usuario != null && actividades != null) {
                val actividadesIds = ArrayList(actividades.map { it.idActividadProgramada })
                val intent = Intent(this, DatosPagoActivity::class.java).apply {
                    putExtra("idUsuario", usuario.idUsuario)
                    putExtra("tipo", "noSocio")
                    putIntegerArrayListExtra("actividades", actividadesIds)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Buscá y seleccioná un usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarUsuario(usuario: Usuario) {
        usuarioSeleccionado = usuario
        val mensajeResultado = findViewById<TextView>(R.id.mensaje_resultado)
        val numeroUsuario = findViewById<TextView>(R.id.numero_usuario)
        val textViewNombre = findViewById<TextView>(R.id.textView4)
        val textViewDocumento = findViewById<TextView>(R.id.textView5)
        val textViewEstado = findViewById<TextView>(R.id.textView6)

        mensajeResultado.text = "Mostrando resultado para: "
        numeroUsuario.text = usuario.documento
        textViewNombre.text = "${usuario.nombre.uppercase()} ${usuario.apellido.uppercase()}"
        textViewDocumento.text = "DOCUMENTO: ${usuario.documento}"
        val tipoUsuario = when (usuario.idTipoUsuario) {
            1 -> "ADMINISTRADOR"
            2 -> "SOCIO"
            3 -> "NO SOCIO"
            else -> "DESCONOCIDO"
        }

        textViewEstado.text = "ESTADO: $tipoUsuario"
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