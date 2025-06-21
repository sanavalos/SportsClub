package com.example.sportsclub.database

import android.content.ContentValues
import android.content.Context
import com.example.sportsclub.models.ActividadProgramada
import com.example.sportsclub.models.DatosActividadProgramada
import com.example.sportsclub.models.Pago
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PagoRepository(private val context: Context) {
    private val dbHelper = UserDBHelper(context)

    fun registrarPagoSocio(pago: Pago, estado: Int): Boolean {
        val db = dbHelper.writableDatabase
        var success = false

        val fechaVencimientoActual: Date? = if (estado == 3 || estado == 4) {
            SocioRepository(context).obtenerFechaVencimientoCuotaSocio(pago.idUsuario)
        } else null

        db.beginTransaction()
        try {
            val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val fechaPagoFormateada = pago.fechaPago?.let { formato.format(it) }

            val pagoValues = ContentValues().apply {
                put("id_forma_pago", pago.idFormaPago)
                put("id_usuario", pago.idUsuario)
                put("fecha_pago", fechaPagoFormateada)
                put("monto", pago.monto)
                put("cant_cuotas", pago.cantCuotas)
            }

            val pagoId = db.insert("Pagos", null, pagoValues)
            if (pagoId == -1L) return false

            when (estado) {
                1, 2 -> {
                    val calendar = Calendar.getInstance().apply {
                        time = pago.fechaPago ?: Date()
                        add(Calendar.MONTH, 1)
                    }

                    val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val nuevaFecha = formato.format(calendar.time)

                    val updateSocio = ContentValues().apply {
                        put("fecha_vencimiento", nuevaFecha)
                    }

                    db.update(
                        "Socios",
                        updateSocio,
                        "id_usuario = ?",
                        arrayOf(pago.idUsuario.toString())
                    )
                }

                3, 4 -> {
                    val nuevaFechaDate = Calendar.getInstance().apply {
                        time = fechaVencimientoActual ?: Date()
                        add(Calendar.MONTH, 1)
                    }.time

                    val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val nuevaFecha = formato.format(nuevaFechaDate)

                    val updateSocio = ContentValues().apply {
                        put("fecha_vencimiento", nuevaFecha)
                    }

                    db.update(
                        "Socios",
                        updateSocio,
                        "id_usuario = ?",
                        arrayOf(pago.idUsuario.toString())
                    )
                }
            }
            db.setTransactionSuccessful()
            success = true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }
        return success
    }

    fun obtenerMontoTotalPorActividades(idActividadesProgramadas: List<Int>): Double {
        if (idActividadesProgramadas.isEmpty()) return 0.0

        val db = dbHelper.readableDatabase
        var total = 0.0

        var cadenaConTotalidadDeArgumentos = ""
        val argumentosEnterosAString = mutableListOf<String>()

        for (i in idActividadesProgramadas.indices) {
            cadenaConTotalidadDeArgumentos += "?"
            if (i != idActividadesProgramadas.size - 1) {
                cadenaConTotalidadDeArgumentos += ", "
            }
            argumentosEnterosAString.add(idActividadesProgramadas[i].toString())
        }

        val query = """
        SELECT SUM(a.precio)
        FROM Actividades_Programadas ap
        JOIN Actividades a ON ap.id_actividad = a.id_actividad
        WHERE ap.id_actividad_programada IN ($cadenaConTotalidadDeArgumentos)
    """.trimIndent()

        val cursor = db.rawQuery(query, argumentosEnterosAString.toTypedArray())

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }

        cursor.close()
        db.close()
        return total
    }

    fun registrarPagoNoSocio(pago: Pago, idsActividades: List<Int>): Boolean {
        if (idsActividades.isEmpty()) return false

        val db = dbHelper.writableDatabase
        var success = false

        db.beginTransaction()
        try {
            val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val fechaPagoFormateada = pago.fechaPago?.let { formato.format(it) }

            val pagoValues = ContentValues().apply {
                put("id_forma_pago", pago.idFormaPago)
                put("id_usuario", pago.idUsuario)
                put("fecha_pago", fechaPagoFormateada)
                put("monto", pago.monto)
                put("cant_cuotas", pago.cantCuotas)
            }

            val idPago = db.insert("Pagos", null, pagoValues)
            if (idPago == -1L) return false

            for (idActividad in idsActividades) {
                val actividadValues = ContentValues().apply {
                    put("id_actividad_programada", idActividad)
                    put("id_pago", idPago)
                }
                val resultado = db.insert("Actividades_Contratadas", null, actividadValues)
                if (resultado == -1L) throw Exception("Error al insertar actividad contratada")
            }

            db.setTransactionSuccessful()
            success = true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }

        return success
    }

    fun obtenerActividadesProgramadasPorPago(idPago: Int): List<DatosActividadProgramada> {
        val db = dbHelper.readableDatabase
        val actividadesProgramadas = mutableListOf<DatosActividadProgramada>()

        val query = """
        SELECT ap.id_actividad_programada, a.nombre_actividad, ap.fecha_hora
        FROM Actividades_Contratadas ac
        JOIN Actividades_Programadas ap ON ac.id_actividad_programada = ap.id_actividad_programada
        JOIN Actividades a ON a.id_actividad = ap.id_actividad
        WHERE ac.id_pago = ?
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(idPago.toString()))

        if (cursor.moveToFirst()) {
            do {
                val idActividadProgramada = cursor.getInt(0)
                val nombreActividad = cursor.getString(1)
                val fechaStr = cursor.getString(2)

                var fecha: Date? = null
                if (!fechaStr.isNullOrEmpty())
                {
                    val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    fecha = formato.parse(fechaStr)
                }
                actividadesProgramadas.add(DatosActividadProgramada(idActividadProgramada, nombreActividad,fecha))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return actividadesProgramadas
    }
}