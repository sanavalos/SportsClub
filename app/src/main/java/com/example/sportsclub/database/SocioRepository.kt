package com.example.sportsclub.database

import android.content.ContentValues
import android.content.Context
import com.example.sportsclub.models.DatosSocio
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.sportsclub.models.CarnetSocio
import com.example.sportsclub.models.Socio


class SocioRepository(context: Context) {
    private val dbHelper = UserDBHelper(context)

    fun obtenerFechaVencimientoCuotaSocio(idUsuario: Int): Date? {
        var fechaVencimiento: Date? = null
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT fecha_vencimiento FROM Socios WHERE id_usuario = ?",
            arrayOf(idUsuario.toString())
        )

        if (cursor.moveToFirst()) {
            val fechaVencimientoStr = cursor.getString(0)

            if (!fechaVencimientoStr.isNullOrEmpty())
            {
                val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                fechaVencimiento = formato.parse(fechaVencimientoStr)
            } else
            {
                fechaVencimiento = null
            }

        }

        cursor.close()
        db.close()
        return fechaVencimiento
    }

    fun obtenerMontoMensualPlanSocio(idUsuario: Int): Double? {
        var montoMensual: Double? = null
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT p.monto_mensual FROM Socios s JOIN Planes p ON p.id_plan = s.id_plan WHERE id_usuario = ?",
            arrayOf(idUsuario.toString())
        )

        if (cursor.moveToFirst()) {
            montoMensual = cursor.getDouble(0)
        }

        cursor.close()
        db.close()
        return montoMensual
    }

    fun obtenerSociosConVencimientoHoy(): List<DatosSocio> {
        val sociosConVencimientoHoy = mutableListOf<DatosSocio>()
        val db = dbHelper.readableDatabase

        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaHoy = formato.format(Date())

        val cursor = db.rawQuery(
            """
        SELECT u.id_usuario ,u.nombre, u.apellido, u.documento, s.imagen_carnet
        FROM Usuarios u
        JOIN Socios s ON u.id_usuario = s.id_usuario
        WHERE s.fecha_vencimiento = ?
        """.trimIndent(),
            arrayOf(fechaHoy)
        )

        if (cursor.moveToFirst()) {
            do {
                val idUsuario = cursor.getInt(0)
                val nombre = cursor.getString(1)
                val apellido = cursor.getString(2)
                val documento = cursor.getString(3)
                val imagenCarnet = cursor.getBlob(4)

                sociosConVencimientoHoy.add(DatosSocio(idUsuario, nombre, apellido, documento, imagenCarnet))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return sociosConVencimientoHoy
    }
    fun obtenerDatosSocioPorDocumento(documento: String): CarnetSocio? {
        val db = dbHelper.readableDatabase
        var datosSocioCarnet: CarnetSocio? = null

        val query = """
        SELECT u.nombre, u.apellido, u.documento, t.nombre_tipo_documento,
               s.nro_carnet, s.imagen_carnet
        FROM Usuarios u
        INNER JOIN Socios s ON u.id_usuario = s.id_usuario
        INNER JOIN Tipos_Documentos t ON t.id_tipo_documento = u.id_tipo_documento
        WHERE u.documento = ?
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(documento))

        if (cursor.moveToFirst()) {
            datosSocioCarnet = CarnetSocio(
                nombre = cursor.getString(0),
                apellido = cursor.getString(1),
                documento = cursor.getString(2),
                nombreTipoDocumento = cursor.getString(3),
                nroCarnet = cursor.getString(4),
                imagenCarnet = cursor.getBlob(5)
            )
        }

        cursor.close()
        db.close()

        return datosSocioCarnet
    }

    fun insertarSocio(socio: Socio): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("id_usuario", socio.idUsuario)
            put("id_plan", socio.idPlan)
            put("nro_carnet", socio.nroCarnet)
            put("fecha_vencimiento", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(socio.fechaVencimiento))
            put("imagen_carnet", socio.imagenCarnet)
        }

        return db.insert("Socios", null, values)
    }

}