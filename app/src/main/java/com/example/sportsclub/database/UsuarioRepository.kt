package com.example.sportsclub.database

import android.content.ContentValues
import android.content.Context
import com.example.sportsclub.models.Usuario

class UsuarioRepository(context: Context) {
    private val dbHelper = UserDBHelper(context)

    fun crearNoSocio(usuario: Usuario): Boolean {
        val db = dbHelper.writableDatabase
        var success = false

        try {
            val userValues = ContentValues().apply {
                put("id_tipo_documento", usuario.idTipoDocumento)
                put("id_tipo_usuario", usuario.idTipoUsuario)
                put("entrego_apto_fisico", usuario.entregoAptoFisico?.let { if (it) 1 else 0 })
                put("nombre", usuario.nombre)
                put("apellido", usuario.apellido)
                put("documento", usuario.documento)
                put("telefono", usuario.telefono)
                put("direccion", usuario.direccion)
            }

            val userId = db.insert("Usuarios", null, userValues)
            if (userId == -1L) return false

            success = true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return success
    }

}