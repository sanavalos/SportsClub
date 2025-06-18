package com.example.sportsclub.database

import android.content.Context
import com.example.sportsclub.models.TipoDocumento

class TipoDocumentoRepository(context: Context) {
    private val dbHelper = UserDBHelper(context)

    fun obtenerTiposDocumento(): List<TipoDocumento> {
        val tipos = mutableListOf<TipoDocumento>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id_tipo_documento, nombre_tipo_documento FROM Tipos_Documentos", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val nombre = cursor.getString(1)
                tipos.add(TipoDocumento(idTipoDocumento = id, nombreTipoDocumento = nombre))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return tipos
    }
}