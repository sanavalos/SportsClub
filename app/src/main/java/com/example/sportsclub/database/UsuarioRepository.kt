package com.example.sportsclub.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.sportsclub.models.Usuario

class UsuarioRepository(context: Context) {
    val dbHelper = UserDBHelper(context)

    fun insertarUsuario(usuario: Usuario): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("id_tipo_documento", usuario.idTipoDocumento)
            put("id_tipo_usuario", usuario.idTipoUsuario)
            put("entrego_apto_fisico", if (usuario.entregoAptoFisico == true) 1 else 0)
            put("nombre", usuario.nombre)
            put("apellido", usuario.apellido)
            put("documento", usuario.documento)
            put("telefono", usuario.telefono)
            put("direccion", usuario.direccion)
        }

        return db.insert("Usuarios", null, values)
    }

}