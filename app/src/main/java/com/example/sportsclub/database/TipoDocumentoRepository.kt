package com.example.sportsclub.database

import android.content.Context

class TipoDocumentoRepository(context: Context) {
    private val dbHelper = UserDBHelper(context)

    fun obtenerIdTipoDocumento(nombre: String): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id_tipo_documento FROM Tipos_Documentos WHERE nombre_tipo_documento = ?",
            arrayOf(nombre)
        )
        val id = if (cursor.moveToFirst()) {
            cursor.getInt(0)
        } else {
            -1
        }
        cursor.close()
        return id
    }
}