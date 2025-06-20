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
import android.widget.Toast
import com.example.sportsclub.R
import com.example.sportsclub.database.SocioRepository
import com.example.sportsclub.database.UsuarioRepository
import com.example.sportsclub.models.Pago
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class PaymentListActivity : AppCompatActivity() {
    private var estadoPago: Int = 0
    private var pagoPendiente: Pago? = null
    private lateinit var adapterSocio: PaymentAdapter
    private lateinit var adapterNoSocio: PaymentNoSocioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
            val seleccion = adapterSocio.obtenerPagoSeleccionado()

            if (seleccion != null) {
                val (pago, estado) = seleccion

                val intent = Intent(this, DatosPagoActivity::class.java).apply {
                    putExtra("idUsuario", pago.idUsuario)
                    putExtra("estadoPago", estado)
                    putExtra("tipo", "socio")
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Debe seleccionar una cuota a pagar", Toast.LENGTH_SHORT).show()
            }
        }

        fun realizarBusqueda() {
            val dni = searchInput.text.toString()

            if (dni.isNotEmpty()) {
                val repo = UsuarioRepository(this)
                val usuarioEncontrado = repo.obtenerUsuarioYPagosPorDocumento(dni)

                if (usuarioEncontrado != null) {

                    if (usuarioEncontrado.idTipoUsuario == 2) {

                        labelBusquedaGenerico.visibility = View.VISIBLE
                        labelBusqueda.text = formatearDNI(dni)
                        labelBusqueda.visibility = View.VISIBLE
                        userCard.visibility = View.VISIBLE
                        recyclerView.visibility = View.VISIBLE
                        recyclerViewNoSocio.visibility = View.GONE
                        nextButton.visibility = View.VISIBLE

                        val nombre = usuarioEncontrado.nombre
                        val apellido = usuarioEncontrado.apellido
                        val dniUsuario = usuarioEncontrado.documento
                        val rol = usuarioEncontrado.nombreTipoUsuario
                        val pagos = usuarioEncontrado.pagos

                        textNombre.text = "${nombre} ${apellido}"
                        textDocumento.text = "DOCUMENTO: ${formatearDNI(dniUsuario)}"
                        textEstado.text = "ESTADO: ${rol}"

                        if (pagos != null) {
                            val socioRepository = SocioRepository(this)
                            val fechaVencimiento =  socioRepository.obtenerFechaVencimientoCuotaSocio(usuarioEncontrado.idUsuario)

                            if(fechaVencimiento == null && usuarioEncontrado.pagos.isEmpty())
                            {
                                estadoPago = 1
                                pagoPendiente = Pago(
                                    idPago = -1,
                                    idFormaPago = -1 ,
                                    idUsuario = usuarioEncontrado.idUsuario,
                                    fechaPago = Date(),
                                    monto = -1.0,
                                    cantCuotas = -1
                                )
                            } else
                            {
                                val hoy = resetHora(Calendar.getInstance())
                                val vencimiento = resetHora(Calendar.getInstance()).apply { time = fechaVencimiento }

                                if (hoy.after(vencimiento)) {
                                    estadoPago = 2
                                    pagoPendiente = Pago(
                                        idPago = -1,
                                        idFormaPago = -1 ,
                                        idUsuario = usuarioEncontrado.idUsuario,
                                        fechaPago = fechaVencimiento,
                                        monto = -1.0,
                                        cantCuotas = -1
                                    )
                                } else {
                                    val millisDiferencia = vencimiento.timeInMillis - hoy.timeInMillis
                                    val diasRestantes = (millisDiferencia / (1000 * 60 * 60 * 24)).toInt()

                                    when {
                                        diasRestantes in 0..7 -> {
                                            estadoPago = 3
                                            pagoPendiente = Pago(
                                                idPago = -1,
                                                idFormaPago = -1,
                                                idUsuario = usuarioEncontrado.idUsuario,
                                                fechaPago = fechaVencimiento,
                                                monto = -1.0,
                                                cantCuotas = -1
                                            )
                                        }
                                        diasRestantes in 8..30 -> {
                                            estadoPago = 4
                                            pagoPendiente = Pago(
                                                idPago = -1,
                                                idFormaPago = -1,
                                                idUsuario = usuarioEncontrado.idUsuario,
                                                fechaPago = fechaVencimiento,
                                                monto = -1.0,
                                                cantCuotas = -1
                                            )
                                        }
                                    }
                                }
                            }

                            val pagosVisuales = normalizarFechasParaVisualizacion(pagos)
                                .sortedByDescending { it.second }

                            val fechasOcupadas = pagosVisuales.map {
                                SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(it.second)
                            }.toSet()

                            val pagoDinamicoVisual = pagoPendiente?.let {
                                val fechaVisual = calcularFechaVisualParaDinamico(it, fechasOcupadas)
                                it to fechaVisual
                            }

                            adapterSocio = PaymentAdapter(
                                pagosVisuales,
                                pagoDinamicoVisual,
                                estadoPago
                            ) { pago, estado ->
                                Toast.makeText(this, "Seleccionado: ${pago.idPago}, estado: $estado", Toast.LENGTH_SHORT).show()
                            }

                            recyclerView.adapter = adapterSocio
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

                        val nombre = usuarioEncontrado.nombre
                        val apellido = usuarioEncontrado.apellido
                        val dniUsuario = usuarioEncontrado.documento
                        val rol = usuarioEncontrado.nombreTipoUsuario
                        val pagos = usuarioEncontrado.pagos

                        textNombre.text = "${nombre} ${apellido}"
                        textDocumento.text = "DOCUMENTO: ${formatearDNI(dniUsuario)}"
                        textEstado.text = "ESTADO: ${rol}"

                        if (pagos != null) {
                            adapterNoSocio = PaymentNoSocioAdapter(pagos){ pagoSeleccionado ->
                                Toast.makeText(this, "Pago seleccionado: ${pagoSeleccionado.idPago}", Toast.LENGTH_SHORT).show()
                            }

                            recyclerViewNoSocio.adapter = adapterNoSocio
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

    fun resetHora(cal: Calendar): Calendar {
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal
    }

    fun normalizarFechasParaVisualizacion(pagos: List<Pago>): List<Pair<Pago, Date>> {
        val formato = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val fechasAsignadas = mutableSetOf<String>()
        val resultado = mutableListOf<Pair<Pago, Date>>()

        for (pago in pagos.sortedBy { it.fechaPago }) {
            var fecha = pago.fechaPago ?: continue
            val calendar = Calendar.getInstance().apply { time = fecha }

            while (formato.format(calendar.time) in fechasAsignadas) {
                calendar.add(Calendar.MONTH, 1)
            }

            fechasAsignadas.add(formato.format(calendar.time))
            resultado.add(pago to calendar.time)
        }

        return resultado
    }

    fun calcularFechaVisualParaDinamico(pago: Pago, fechasOcupadas: Set<String>): Date {
        val formato = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val calendar = Calendar.getInstance().apply { time = pago.fechaPago ?: Date() }

        while (formato.format(calendar.time) in fechasOcupadas) {
            calendar.add(Calendar.MONTH, 1)
        }

        return calendar.time
    }
}