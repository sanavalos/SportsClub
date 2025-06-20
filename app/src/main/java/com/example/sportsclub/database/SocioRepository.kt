package com.example.sportsclub.database

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
}