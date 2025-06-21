package com.example.sportsclub.models

import java.util.Date

data class DatosActividadProgramada (
    val idActividadProgramada: Int = 0,
    val nombreActividad: String,
    val fechaHora: Date?
)