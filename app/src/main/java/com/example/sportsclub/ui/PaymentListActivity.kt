package com.example.sportsclub.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportsclub.adapters.PaymentAdapter
import com.example.sportsclub.adapters.PaymentNoSocioAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.appcompat.widget.AppCompatButton
import android.widget.ImageView
import android.view.inputmethod.EditorInfo
import android.view.View
import android.content.Intent
import com.example.sportsclub.R


class PaymentListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val usuarios = listOf(
            mapOf(
                "nombre" to "JUAN PEREZ",
                "dni" to "12345678",
                "rol" to "Socio",
                "pagos" to listOf(
                    mapOf("mes" to "Enero 2025", "estado" to true),
                    mapOf("mes" to "Febrero 2025", "estado" to false),
                    mapOf("mes" to "Marzo 2025", "estado" to true),
                    mapOf("mes" to "Abril 2025", "estado" to false)
                )
            ),
            mapOf(
                "nombre" to "JOSÉ RODRIGUEZ",
                "dni" to "87654321",
                "rol" to "No Socio",
                "pagos" to listOf(
                    mapOf("actividad" to "Fútbol", "fecha" to "12/04/2025 17:00hs"),
                    mapOf("actividad" to "Voley", "fecha" to "04/04/2025 12:00hs"),
                    mapOf("actividad" to "Fútbol", "fecha" to "04/04/2025 17:00hs"),
                    mapOf("actividad" to "Tenis", "fecha" to "25/03/2025 18:00hs"),
                    mapOf("actividad" to "Básquetbol", "fecha" to "10/02/2025 14:00hs")
                )
            ),
            mapOf(
                "nombre" to "JULIANA GONZALEZ",
                "dni" to "23456789",
                "rol" to "Socio",
                "pagos" to listOf(
                    mapOf("mes" to "Mayo 2025", "estado" to true),
                    mapOf("mes" to "Junio 2025", "estado" to false),
                    mapOf("mes" to "Julio 2025", "estado" to false),
                    mapOf("mes" to "Agosto 2025", "estado" to true),
                    mapOf("mes" to "Septiembre 2025", "estado" to true)
                )
            )
        )

        fun formatearDNI(dni: String): String {
            return dni.reversed()
                .chunked(3)
                .joinToString(".")
                .reversed()
        }

        val searchInput = findViewById<EditText>(R.id.searchInput)
        val searchIcon = findViewById<ImageView>(R.id.searchIcon)
        val labelBusquedaGenerico = findViewById<TextView>(R.id.labelBusquedaGenerico)
        val labelBusqueda = findViewById<TextView>(R.id.labelBusqueda)
        val userCard = findViewById<CardView>(R.id.userCard)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerPagos)
        val recyclerViewNoSocio = findViewById<RecyclerView>(R.id.recyclerPagosNoSocio)
        val nextButton = findViewById<AppCompatButton>(R.id.next)
        val textNombre = findViewById<TextView>(R.id.textNombre)
        val textDocumento = findViewById<TextView>(R.id.textDocumento)
        val textEstado = findViewById<TextView>(R.id.textEstado)
        val backMenu = findViewById<ImageView>(R.id.backMenu)

        backMenu.setOnClickListener {
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)
        }

        nextButton.setOnClickListener {
            val intent = Intent(this, DatosPagoActivity::class.java)
            startActivity(intent)
        }

        fun realizarBusqueda() {
            val dni = searchInput.text.toString()

            if (dni.isNotEmpty()) {
                val usuarioEncontrado = usuarios.find { it["dni"] == dni }

                if (usuarioEncontrado != null) {

                    if (usuarioEncontrado["rol"]?.toString()?.equals("Socio", ignoreCase = true) == true) {

                        labelBusquedaGenerico.visibility = View.VISIBLE
                        labelBusqueda.text = formatearDNI(dni)
                        labelBusqueda.visibility = View.VISIBLE
                        userCard.visibility = View.VISIBLE
                        recyclerView.visibility = View.VISIBLE
                        recyclerViewNoSocio.visibility = View.GONE
                        nextButton.visibility = View.VISIBLE

                        val nombre = usuarioEncontrado["nombre"] as? String ?: ""
                        val dniUsuario = usuarioEncontrado["dni"] as? String ?: ""
                        val rol = usuarioEncontrado["rol"] as? String ?: ""

                        textNombre.text = nombre
                        textDocumento.text = "DOCUMENTO: ${formatearDNI(dniUsuario)}"
                        textEstado.text = "ESTADO: ${rol}"

                        val pagosMap = usuarioEncontrado["pagos"] as? List<Map<String, Any>>
                        if (pagosMap != null) {
                            val pagos = pagosMap.map { pago ->
                                val mes = pago["mes"] as? String ?: ""
                                val estado = pago["estado"] as? Boolean ?: false
                                mes to estado
                            }

                            val adapter = PaymentAdapter(pagos)

                            recyclerView.adapter = adapter
                            recyclerView.layoutManager = LinearLayoutManager(this)
                        }
                    } else {
                        labelBusquedaGenerico.visibility = View.VISIBLE
                        labelBusqueda.text = formatearDNI(dni)
                        labelBusqueda.visibility = View.VISIBLE
                        userCard.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                        recyclerViewNoSocio.visibility = View.VISIBLE
                        nextButton.visibility = View.GONE

                        val nombre = usuarioEncontrado["nombre"] as? String ?: ""
                        val dniUsuario = usuarioEncontrado["dni"] as? String ?: ""
                        val rol = usuarioEncontrado["rol"] as? String ?: ""

                        textNombre.text = nombre
                        textDocumento.text = "DOCUMENTO: ${formatearDNI(dniUsuario)}"
                        textEstado.text = "ESTADO: ${rol}"

                        val pagosMap = usuarioEncontrado["pagos"] as? List<Map<String, Any>>
                        if (pagosMap != null) {
                            val pagos = pagosMap.map { pago ->
                                val actividad = pago["actividad"] as? String ?: ""
                                val fecha = pago["fecha"] as? String ?: ""
                                actividad to fecha
                            }

                            val adapter = PaymentNoSocioAdapter(pagos)

                            recyclerViewNoSocio.adapter = adapter
                            recyclerViewNoSocio.layoutManager = LinearLayoutManager(this)
                        }
                    }
                } else {
                    labelBusqueda.text = dni
                    userCard.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    recyclerViewNoSocio.visibility = View.GONE
                    nextButton.visibility = View.GONE
                }
            }
        }

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                realizarBusqueda()
                true
            } else {
                false
            }
        }

        searchIcon.setOnClickListener {
            realizarBusqueda()
        }
    }
}