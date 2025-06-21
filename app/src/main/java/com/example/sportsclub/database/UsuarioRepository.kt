package com.example.sportsclub.database

import android.content.ContentValues
import android.content.Context
import com.example.sportsclub.models.Pago
import com.example.sportsclub.models.UsuarioPago
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.sportsclub.models.Usuario

class UsuarioRepository(context: Context) {
    private val dbHelper = UserDBHelper(context)

    fun existeUsuarioPorTipoYDocumento(idTipoDocumento: Int, documento: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT 1 FROM Usuarios WHERE id_tipo_documento = ? AND documento = ? LIMIT 1",
            arrayOf(idTipoDocumento.toString(), documento)
        )

        val existe = cursor.moveToFirst()

        cursor.close()
        db.close()

        return existe
    }

    fun obtenerUsuarioYPagosPorDocumento(doc: String): UsuarioPago? {
        val db = dbHelper.readableDatabase

        val query = """
        SELECT u.id_usuario, u.nombre, u.apellido, u.documento, u.id_tipo_usuario ,t.nombre_tipo_usuario
        FROM Usuarios u
        JOIN Tipos_Usuarios t ON u.id_tipo_usuario = t.id_tipo_usuario
        WHERE u.documento = ?
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(doc))
        var datosUsuarioPago: UsuarioPago? = null

        if (cursor.moveToFirst()) {
            val idUsuario = cursor.getInt(0)
            val nombre = cursor.getString(1)
            val apellido = cursor.getString(2)
            val documento = cursor.getString(3)
            val idTipoUsuario = cursor.getInt(4)
            val nombreTipoUsuario = cursor.getString(5)

            val pagos = obtenerPagosPorIdUsuario(idUsuario)

            datosUsuarioPago = UsuarioPago(idUsuario, nombre, apellido, documento, idTipoUsuario, nombreTipoUsuario , pagos)
        }

        cursor.close()
        db.close()
        return datosUsuarioPago
    }

    fun obtenerPagosPorIdUsuario(usuarioId: Int): List<Pago> {
        val pagos = mutableListOf<Pago>()
        val db = dbHelper.readableDatabase

        val query = """
        SELECT id_pago, id_forma_pago, id_usuario, fecha_pago, monto, cant_cuotas
        FROM Pagos 
        WHERE id_usuario = ?
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(usuarioId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val idPago = cursor.getInt(0)
                val idFormaPago = cursor.getInt(1)
                val idUsuario = cursor.getInt(2)
                val fechaPagoStr = cursor.getString(3)
                val monto = cursor.getDouble(4)
                val cantCuotas = cursor.getInt(5)

                val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val fechaPago: Date? = formato.parse(fechaPagoStr)

                pagos.add(Pago(idPago = idPago, idFormaPago = idFormaPago, idUsuario = idUsuario, fechaPago = fechaPago, monto = monto, cantCuotas = cantCuotas))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return pagos
    }

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