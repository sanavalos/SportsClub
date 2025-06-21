package com.example.sportsclub.database

import android.content.Context

class LoginRepository (context: Context){
    private val dbHelper = UserDBHelper(context)

    fun login(email: String, pass: String): Boolean {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM Administradores WHERE email = ? AND pass = ?",
            arrayOf(email, pass)
        )

        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }

}