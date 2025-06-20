package com.example.sportsclub.models

import java.io.Serializable
import java.util.Date

data class ActividadProgramada(
    val idActividadProgramada: Int = 0,
    val idActividad: Int,
    val fechaHora: Date?,
    val cupo: Int
): Serializable