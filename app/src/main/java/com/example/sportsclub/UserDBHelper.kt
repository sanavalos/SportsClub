package com.example.sportsclub

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDBHelper(context: Context) : SQLiteOpenHelper(context, "UsuariosDB", null, 1){

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE adminUsuario (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                email TEXT UNIQUE,
                contrasenia TEXT 
            )
        """.trimIndent())

        db.execSQL("INSERT INTO adminUsuario (email, contrasenia) VALUES ('juana.perez@clubdeportivo.com', 'prueba1234')")
        db.execSQL("INSERT INTO adminUsuario (email, contrasenia) VALUES ('rodrigo1993@clubdeportivo.com', 'prueba34566')")
        db.execSQL("INSERT INTO adminUsuario (email, contrasenia) VALUES ('alex_admin@clubdeportivo.com', 'prueba7891')")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }


    fun login(email: String, contrasenia: String): Boolean {
        var db = readableDatabase
        var cursor = db.rawQuery(
            "SELECT * FROM adminUsuario WHERE email = ? AND contrasenia = ?",
            arrayOf(email, contrasenia)
        )

        var existe = cursor.count > 0
        return existe
    }


}