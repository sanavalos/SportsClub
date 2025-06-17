package com.example.sportsclub.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UserDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "SportsClub.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("PRAGMA foreign_keys=ON")

        db?.execSQL("""
        CREATE TABLE Tipos_Usuarios (
            id_tipo_usuario INTEGER PRIMARY KEY,
            nombre_tipo_usuario TEXT
        )
    """.trimIndent())

        db?.execSQL("""
        INSERT INTO Tipos_Usuarios VALUES 
        (1, 'Administrador'),
        (2, 'Socio'),
        (3, 'No Socio')
    """.trimIndent())

        db?.execSQL("""
        CREATE TABLE Tipos_Documentos (
            id_tipo_documento INTEGER PRIMARY KEY,
            nombre_tipo_documento TEXT
        )
    """.trimIndent())

        db?.execSQL("""
        INSERT INTO Tipos_Documentos VALUES
        (1, 'DNI'),
        (2, 'Pasaporte'),
        (3, 'LC')
    """.trimIndent())

        db?.execSQL("""
        CREATE TABLE Formas_De_Pago (
            id_forma_pago INTEGER PRIMARY KEY,
            nombre_forma_pago TEXT
        )
    """.trimIndent())

        db?.execSQL("""
        INSERT INTO Formas_De_Pago VALUES
        (1, 'Tarjeta de Crédito'),
        (2, 'Efectivo')
    """.trimIndent())

        db?.execSQL("""
        CREATE TABLE Actividades (
            id_actividad INTEGER PRIMARY KEY,
            nombre_actividad TEXT,
            precio REAL
        )
    """.trimIndent())

        db?.execSQL("""
        CREATE TABLE Actividades_Programadas (
            id_actividad_programada INTEGER PRIMARY KEY AUTOINCREMENT,
            id_actividad INTEGER NOT NULL,
            fecha_hora TEXT NOT NULL,
            cupo INTEGER,
            FOREIGN KEY(id_actividad) REFERENCES Actividades(id_actividad)
        )
    """.trimIndent())

        db?.execSQL("""
        INSERT INTO Actividades VALUES
        (1, 'Fútbol', 10000),
        (2, 'Natación', 15000),
        (3, 'Handball', 12500),
        (4, 'Basketball', 12500),
        (5, 'Tenis', 8000),
        (6, 'Gimnasia', 5000),
        (7, 'Yoga', 6000),
        (8, 'Zumba', 5500),
        (9, 'Voleibol', 9000),
        (10, 'Kickboxing', 7500),
        (11, 'Pilates', 7000),
        (12, 'Crossfit', 12000),
        (13, 'Boxeo', 8500),
        (14, 'Escalada', 11000)
    """.trimIndent())

        db?.execSQL("""
        CREATE TABLE Usuarios (
            id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
            id_tipo_documento INTEGER NOT NULL,
            id_tipo_usuario INTEGER NOT NULL,
            entrego_apto_fisico INTEGER,
            nombre TEXT,
            apellido TEXT,
            documento TEXT,
            telefono TEXT,
            direccion TEXT,
            FOREIGN KEY(id_tipo_documento) REFERENCES Tipos_Documentos(id_tipo_documento),
            FOREIGN KEY(id_tipo_usuario) REFERENCES Tipos_Usuarios(id_tipo_usuario)
        )
    """.trimIndent())

        db?.execSQL("""
        INSERT INTO Usuarios (id_tipo_documento, id_tipo_usuario, entrego_apto_fisico, nombre, apellido, documento, telefono, direccion)
        VALUES (1, 1, null, 'Juan', 'Pérez', '12345678', '123456789', 'Av. Libertador 487')
    """.trimIndent())

        db?.execSQL("""
        INSERT INTO Usuarios (id_tipo_documento, id_tipo_usuario, entrego_apto_fisico, nombre, apellido, documento, telefono, direccion)
        VALUES (1, 1, null, 'María', 'Rodriguez', '87654321', '987654321', 'Av. Belgrano 941')
    """.trimIndent())

        db?.execSQL("""
        CREATE TABLE Pagos (
            id_pago INTEGER PRIMARY KEY AUTOINCREMENT,
            id_forma_pago INTEGER NOT NULL,
            id_usuario INTEGER NOT NULL,
            fecha_pago DATE,
            monto REAL,
            cant_cuotas INTEGER,
            FOREIGN KEY(id_forma_pago) REFERENCES Formas_De_Pago(id_forma_pago),
            FOREIGN KEY(id_usuario) REFERENCES Usuarios(id_usuario)
        )
    """.trimIndent())

        db?.execSQL("""
        CREATE TABLE Actividades_Contratadas (
          id_actividad_contratada INTEGER PRIMARY KEY AUTOINCREMENT,
          id_actividad_programada INTEGER NOT NULL,
          id_pago INTEGER, 
          FOREIGN KEY(id_actividad_programada) REFERENCES Actividades_Programadas(id_actividad_programada),
          FOREIGN KEY(id_pago) REFERENCES Pagos(id_pago) 
        )
    """.trimIndent())

        db?.execSQL("""
        CREATE TABLE Planes (
            id_plan INTEGER PRIMARY KEY,
            descripcion TEXT,
            monto_mensual REAL
        )
    """.trimIndent())

        db?.execSQL("""
        INSERT INTO Planes VALUES
        (1, '2 veces por semana', 34000),
        (2, '3 veces por semana', 43000),
        (3, 'Libre', 56000)
    """.trimIndent())

        db?.execSQL("""
        CREATE TABLE Socios (
            id_usuario INTEGER PRIMARY KEY,
            id_plan INTEGER NOT NULL,
            nro_carnet TEXT,
            fecha_vencimiento DATE,
            imagen_carnet BLOB,
            FOREIGN KEY(id_usuario) REFERENCES Usuarios(id_usuario),
            FOREIGN KEY(id_plan) REFERENCES Planes(id_plan)
        )
    """.trimIndent())

        db?.execSQL("""
        CREATE TABLE Administradores (
            id_usuario INTEGER PRIMARY KEY,
            email TEXT UNIQUE,
            pass TEXT,
            FOREIGN KEY(id_usuario) REFERENCES Usuarios(id_usuario)
        )
    """.trimIndent())

        db?.execSQL("""
        INSERT INTO Administradores (id_usuario, email, pass)
        VALUES (1, 'juan.perez@gmail.com', 'admin123')
    """.trimIndent())

        db?.execSQL("""
        INSERT INTO Administradores (id_usuario, email, pass)
        VALUES (2, 'maria.rodriguez@gmail.com', 'admin456')
    """.trimIndent())

        generarActividadesProgramadas(db!!)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS Administradores")
        db?.execSQL("DROP TABLE IF EXISTS Socios")
        db?.execSQL("DROP TABLE IF EXISTS Planes")
        db?.execSQL("DROP TABLE IF EXISTS Actividades_Contratadas")
        db?.execSQL("DROP TABLE IF EXISTS Actividades_Programadas")
        db?.execSQL("DROP TABLE IF EXISTS Pagos")
        db?.execSQL("DROP TABLE IF EXISTS Usuarios")
        db?.execSQL("DROP TABLE IF EXISTS Actividades")
        db?.execSQL("DROP TABLE IF EXISTS Formas_De_Pago")
        db?.execSQL("DROP TABLE IF EXISTS Tipos_Documentos")
        db?.execSQL("DROP TABLE IF EXISTS Tipos_Usuarios")
        onCreate(db)
    }

    fun generarActividadesProgramadas(db: SQLiteDatabase) {
        val actividades = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)
        val horarios = listOf("08:00", "14:00", "18:00")
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val startCalendar = Calendar.getInstance()

        val endCalendar = Calendar.getInstance()
        endCalendar.set(Calendar.MONTH, Calendar.JULY)
        endCalendar.set(Calendar.DAY_OF_MONTH, 31)
        endCalendar.set(Calendar.HOUR_OF_DAY, 0)
        endCalendar.set(Calendar.MINUTE, 0)
        endCalendar.set(Calendar.SECOND, 0)
        endCalendar.set(Calendar.MILLISECOND, 0)

        db.beginTransaction()
        try {
            while (!startCalendar.after(endCalendar)) {
                val dateStr = dateFormatter.format(startCalendar.time)

                for (actividadId in actividades) {
                    for (hora in horarios) {
                        val fechaHora = "$dateStr $hora:00"
                        val insert = "INSERT INTO Actividades_Programadas (id_actividad, fecha_hora, cupo) VALUES (?, ?, ?)"
                        val stmt = db.compileStatement(insert)
                        stmt.bindLong(1, actividadId.toLong())
                        stmt.bindString(2, fechaHora)
                        stmt.bindLong(3, 20)
                        stmt.executeInsert()
                    }
                }
                startCalendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}