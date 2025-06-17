package com.example.sportsclub.database

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

data class ActividadConHorarios(
    val nombreActividad: String,
    val horarios: List<String>
)

class ActividadRepository(context: Context) {
    private val dbHelper = UserDBHelper(context)

    fun getActividadesConHorarios(): List<ActividadConHorarios> {
        val db = dbHelper.readableDatabase
        val actividadesMap = mutableMapOf<String, MutableList<String>>()

        try {
            val query = """
                SELECT a.nombre_actividad, ap.fecha_hora 
                FROM Actividades a
                INNER JOIN Actividades_Programadas ap ON a.id_actividad = ap.id_actividad
                WHERE DATE(ap.fecha_hora) >= DATE('now')
                ORDER BY a.nombre_actividad, ap.fecha_hora
            """.trimIndent()

            val cursor = db.rawQuery(query, null)

            while (cursor.moveToNext()) {
                val nombreActividad = cursor.getString(cursor.getColumnIndexOrThrow("nombre_actividad"))
                val fechaHora = cursor.getString(cursor.getColumnIndexOrThrow("fecha_hora"))

                val formattedDateTime = formatDateTime(fechaHora)

                if (!actividadesMap.containsKey(nombreActividad)) {
                    actividadesMap[nombreActividad] = mutableListOf()
                }
                actividadesMap[nombreActividad]?.add(formattedDateTime)
            }

            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return actividadesMap.map { (actividad, horarios) ->
            ActividadConHorarios(actividad, horarios)
        }
    }

    fun getActividadesPorFecha(fecha: String): List<ActividadConHorarios> {
        val db = dbHelper.readableDatabase
        val actividadesMap = mutableMapOf<String, MutableList<String>>()

        try {
            val query = """
                SELECT a.nombre_actividad, ap.fecha_hora 
                FROM Actividades a
                INNER JOIN Actividades_Programadas ap ON a.id_actividad = ap.id_actividad
                WHERE DATE(ap.fecha_hora) = ?
                ORDER BY a.nombre_actividad, ap.fecha_hora
            """.trimIndent()

            val cursor = db.rawQuery(query, arrayOf(fecha))

            while (cursor.moveToNext()) {
                val nombreActividad = cursor.getString(cursor.getColumnIndexOrThrow("nombre_actividad"))
                val fechaHora = cursor.getString(cursor.getColumnIndexOrThrow("fecha_hora"))

                val formattedDateTime = formatDateTime(fechaHora)

                if (!actividadesMap.containsKey(nombreActividad)) {
                    actividadesMap[nombreActividad] = mutableListOf()
                }
                actividadesMap[nombreActividad]?.add(formattedDateTime)
            }

            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return actividadesMap.map { (actividad, horarios) ->
            ActividadConHorarios(actividad, horarios)
        }
    }

    fun getAllActividades(): List<String> {
        val db = dbHelper.readableDatabase
        val actividades = mutableListOf<String>()

        try {
            val cursor = db.query(
                "Actividades",
                arrayOf("nombre_actividad"),
                null,
                null,
                null,
                null,
                "nombre_actividad ASC"
            )

            while (cursor.moveToNext()) {
                val nombreActividad = cursor.getString(cursor.getColumnIndexOrThrow("nombre_actividad"))
                actividades.add(nombreActividad)
            }

            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return actividades
    }

    fun getHorariosParaActividad(nombreActividad: String): List<String> {
        val db = dbHelper.readableDatabase
        val horarios = mutableListOf<String>()

        try {
            val query = """
                SELECT ap.fecha_hora 
                FROM Actividades_Programadas ap
                INNER JOIN Actividades a ON ap.id_actividad = a.id_actividad
                WHERE a.nombre_actividad = ? AND DATE(ap.fecha_hora) >= DATE('now')
                ORDER BY ap.fecha_hora
            """.trimIndent()

            val cursor = db.rawQuery(query, arrayOf(nombreActividad))

            while (cursor.moveToNext()) {
                val fechaHora = cursor.getString(cursor.getColumnIndexOrThrow("fecha_hora"))
                horarios.add(formatDateTime(fechaHora))
            }

            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return horarios
    }

    private fun formatDateTime(dbDateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm'hs'", Locale.getDefault())

            val date = inputFormat.parse(dbDateTime)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dbDateTime
        }
    }

    fun getActividadesConPrecios(): Map<String, Double> {
        val db = dbHelper.readableDatabase
        val actividadesPrecios = mutableMapOf<String, Double>()

        try {
            val cursor = db.query(
                "Actividades",
                arrayOf("nombre_actividad", "precio"),
                null,
                null,
                null,
                null,
                "nombre_actividad ASC"
            )

            while (cursor.moveToNext()) {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre_actividad"))
                val precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                actividadesPrecios[nombre] = precio
            }

            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return actividadesPrecios
    }
}