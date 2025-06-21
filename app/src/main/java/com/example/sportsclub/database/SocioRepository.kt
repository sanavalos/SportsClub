package com.example.sportsclub.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.sportsclub.models.Socio
import java.text.SimpleDateFormat
import java.util.Locale

class SocioRepository(context: Context) {
    private val dbHelper = UserDBHelper(context)

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