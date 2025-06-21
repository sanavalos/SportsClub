package com.example.sportsclub.database

import android.content.Context

class PlanRepository(context: Context) {
    private val dbHelper = UserDBHelper(context)

    fun obtenerIdPlan(descripcion: String): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id_plan FROM Planes WHERE descripcion = ?",
            arrayOf(descripcion)
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