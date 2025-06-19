package com.example.sportsclub.database

import android.content.Context
import com.example.sportsclub.models.Usuario

class UsuarioRepository(context: Context) {
    private val dbHelper = UserDBHelper(context)

    fun buscarUsuarioPorDocumento(documento: String): Usuario? {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery("""
            SELECT 
            id_usuario, id_tipo_documento, id_tipo_usuario, entrego_apto_fisico,
            nombre, apellido, documento, telefono, direccion
        FROM Usuarios
        WHERE documento = ? AND id_tipo_usuario = 3
        LIMIT 1
        """.trimIndent(), arrayOf(documento))

        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            usuario = Usuario(
                idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                idTipoDocumento = cursor.getInt(cursor.getColumnIndexOrThrow("id_tipo_documento")),
                idTipoUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("id_tipo_usuario")),
                entregoAptoFisico = when (cursor.getInt(cursor.getColumnIndexOrThrow("entrego_apto_fisico"))) {
                    0 -> false
                    1 -> true
                    else -> null
                },
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                documento = cursor.getString(cursor.getColumnIndexOrThrow("documento")),
                telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion"))
            )

        }

        cursor.close()
        return usuario
    }
}