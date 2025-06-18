package com.example.sportsclub.database

import android.content.Context
import com.example.sportsclub.models.CarnetSocio

class SocioRepository(context: Context) {
    private val dbHelper = UserDBHelper(context)
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
}