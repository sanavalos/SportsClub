package com.example.sportsclub.database

import android.content.Context
import com.example.sportsclub.models.FormaDePago

class FormaDePagoRepository(context: Context) {
    private val dbHelper = UserDBHelper(context)

    fun obtenerFormasDePago(): List<FormaDePago> {
        val lista = mutableListOf<FormaDePago>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id_forma_pago, nombre_forma_pago FROM Formas_De_Pago", null)

        if (cursor.moveToFirst()) {
            do {
                val idFormaPago = cursor.getInt(0)
                val nombreFormaPago = cursor.getString(1)
                lista.add(FormaDePago(idFormaPago, nombreFormaPago))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }
}