package com.example.sportsclub.ui

import android.content.Context
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
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import com.example.sportsclub.R
import com.example.sportsclub.database.SocioRepository
import com.example.sportsclub.database.UsuarioRepository
import com.example.sportsclub.models.Pago
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.graphics.Bitmap
import android.graphics.Canvas
import android.content.ContentValues
import android.provider.MediaStore
import android.os.Build
import com.example.sportsclub.database.PagoRepository
import com.example.sportsclub.models.UsuarioPago


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

            if (dni.isEmpty())
            {
                Toast.makeText(this, "Debe ingresar un documento", Toast.LENGTH_SHORT).show()
            }

            if (dni.isNotEmpty()) {
                val repo = UsuarioRepository(this)
                val usuarioEncontrado = repo.obtenerUsuarioYPagosPorDocumento(dni)

                if (usuarioEncontrado != null) {

                    if (usuarioEncontrado.idTipoUsuario == 2) {

                        labelBusquedaGenerico.text = "Mostrando resultados para:"
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

                        val socioRepository = SocioRepository(this)
                        val fechaVencimiento =  socioRepository.obtenerFechaVencimientoCuotaSocio(usuarioEncontrado.idUsuario)
                        val monto =  socioRepository.obtenerMontoMensualPlanSocio(usuarioEncontrado.idUsuario) ?: 0.0

                        if (pagos != null) {
                            if(fechaVencimiento == null && usuarioEncontrado.pagos.isEmpty())
                            {
                                estadoPago = 1
                                pagoPendiente = Pago(
                                    idPago = -1,
                                    idFormaPago = -1 ,
                                    idUsuario = usuarioEncontrado.idUsuario,
                                    fechaPago = Date(),
                                    monto = monto,
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
                                        monto = monto,
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
                                                monto = monto,
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
                                                monto = monto,
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
                                mostrarDetallePagoSocio(this, pago, usuarioEncontrado, estado, fechaVencimiento)
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
                            val pagosOrdenados = pagos.sortedByDescending { it.fechaPago }

                            adapterNoSocio = PaymentNoSocioAdapter(pagosOrdenados) { pago ->
                                mostrarDetallePagoNoSocio(this, pago, usuarioEncontrado)
                            }
                            recyclerViewNoSocio.adapter = adapterNoSocio
                            recyclerViewNoSocio.layoutManager = LinearLayoutManager(this)
                        }
                    }
                } else {
                    labelBusqueda.text = dni
                    labelBusqueda.visibility = View.VISIBLE
                    labelBusquedaGenerico.visibility = View.VISIBLE
                    labelBusquedaGenerico.text = "No se encontraron resultados para:"
                    labelBusqueda.text = formatearDNI(dni)
                    userCard.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    recyclerViewNoSocio.visibility = View.GONE
                    nextButton.visibility = View.GONE
                    Toast.makeText(this, "No se encontraron ningún usuario", Toast.LENGTH_SHORT).show()
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

    private fun formatearDNI(dni: String): String {
        return dni.reversed()
            .chunked(3)
            .joinToString(".")
            .reversed()
    }

    private fun resetHora(cal: Calendar): Calendar {
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal
    }

    private fun normalizarFechasParaVisualizacion(pagos: List<Pago>): List<Pair<Pago, Date>> {
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

    private fun calcularFechaVisualParaDinamico(pago: Pago, fechasOcupadas: Set<String>): Date {
        val formato = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val calendar = Calendar.getInstance().apply { time = pago.fechaPago ?: Date() }

        while (formato.format(calendar.time) in fechasOcupadas) {
            calendar.add(Calendar.MONTH, 1)
        }

        return calendar.time
    }

    private fun mostrarDetallePagoSocio(context: Context, pago: Pago, usuario: UsuarioPago, estado: Int, fechaVencimiento: Date?) {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.modal_detalle_pago, null)

        val textEstado = view.findViewById<TextView>(R.id.textEstado)
        val textFecha = view.findViewById<TextView>(R.id.textFecha)
        val textNombre = view.findViewById<TextView>(R.id.textNombre)
        val textDocumento = view.findViewById<TextView>(R.id.textDocumento)
        val textActividades = view.findViewById<TextView>(R.id.textActividades)
        val textMonto = view.findViewById<TextView>(R.id.textMonto)
        val btnImprimir = view.findViewById<Button>(R.id.btnImprimir)
        val contenedor = view.findViewById<View>(R.id.contenedorDetallePago)

        textActividades.visibility = View.GONE

        val formato = SimpleDateFormat("d-MM-yyyy HH:mm:ss", Locale.getDefault())
        val fecha = pago.fechaPago?.let { formato.format(it) } ?: "Sin fecha"
        val fechaVencimientoFormateada = formato.format(fechaVencimiento)

        if(estado == 1)
        {
            textEstado.text = "\nEstado: Primera Cuota"
            textFecha.visibility = View.GONE
        } else if(estado == 2)
        {
            textEstado.text = "\nEstado: Vencida"
            textFecha.text = "\nFecha Vencimiento:\n\n $fechaVencimientoFormateada"
        } else if(estado == 3)
        {
            textEstado.text = "\nEstado: Por Vencer"
            textFecha.text = "\nFecha Vencimiento:\n\n $fechaVencimientoFormateada"
        } else if(estado == 4)
        {
            textEstado.text = "\nEstado: Pendiente de Pago"
            textFecha.text = "\nFecha Vencimiento:\n\n $fechaVencimientoFormateada"
        } else
        {
            textEstado.text = "\nEstado: Pagado"
            textFecha.text = "\nFecha Pago:\n\n $fecha"
        }

        textNombre.text = "\nNombre Socio: ${usuario.nombre} ${usuario.apellido}"
        textDocumento.text = "\nDocumento: ${formatearDNI(usuario.documento)}"
        textMonto.text = "\nMonto: $${pago.monto}"

        btnImprimir.setOnClickListener {
            val bitmap = capturarVistaComoBitmap(contenedor)
            val guardado = guardarEnGaleria(context, bitmap)

            val mensaje = if (guardado) "Imagen guardada en Galería" else "Error al guardar imagen"
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()

            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun mostrarDetallePagoNoSocio(context: Context, pago: Pago, usuario: UsuarioPago) {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.modal_detalle_pago, null)

        val textEstado = view.findViewById<TextView>(R.id.textEstado)
        val textFecha = view.findViewById<TextView>(R.id.textFecha)
        val textNombre = view.findViewById<TextView>(R.id.textNombre)
        val textDocumento = view.findViewById<TextView>(R.id.textDocumento)
        val textActividades = view.findViewById<TextView>(R.id.textActividades)
        val textMonto = view.findViewById<TextView>(R.id.textMonto)
        val btnImprimir = view.findViewById<Button>(R.id.btnImprimir)
        val contenedor = view.findViewById<View>(R.id.contenedorDetallePago)

        val formato = SimpleDateFormat("d-MM-yyyy HH:mm:ss", Locale.getDefault())
        val fecha = pago.fechaPago?.let { formato.format(it) } ?: "Sin fecha"

        textEstado.text = "\nEstado: Pagado"
        textFecha.text = "\nFecha Pago:\n\n $fecha"
        textNombre.text = "\nNombre: ${usuario.nombre} ${usuario.apellido}"
        textDocumento.text = "\nDocumento: ${formatearDNI(usuario.documento)}"
        textMonto.text = "Monto: $${pago.monto}"

        val pagoRepository = PagoRepository(context)
        val actividadesContratadas = pagoRepository.obtenerActividadesProgramadasPorPago(pago.idPago)

        val stringBuilder = StringBuilder()
        if(actividadesContratadas.size == 1)
        {
            stringBuilder.append("\nActividad Contratada:\n\n")
        } else
        {
            stringBuilder.append("\nActividades Contratadas:\n\n")
        }

        for (actividad in actividadesContratadas) {
            val fechaFormateada = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(actividad.fechaHora ?: Date())
            stringBuilder.append("• ${actividad.nombreActividad} - ${fechaFormateada} hs\n")
        }

        textActividades.text = stringBuilder.toString()

        btnImprimir.setOnClickListener {
            val bitmap = capturarVistaComoBitmap(contenedor)
            val guardado = guardarEnGaleria(context, bitmap)

            val mensaje = if (guardado) "Imagen guardada en Galería" else "Error al guardar imagen"
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()

            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun capturarVistaComoBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun guardarEnGaleria(context: Context, bitmap: Bitmap): Boolean {
        val resolver = context.contentResolver
        val nombreArchivo = "detalle_pago_${System.currentTimeMillis()}.png"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, nombreArchivo)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/DetallesPagos")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it).use { outputStream ->
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.flush()
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }

            return true
        }

        return false
    }
}