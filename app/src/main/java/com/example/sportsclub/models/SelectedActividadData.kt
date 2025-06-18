package com.example.sportsclub.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectedActividadData(
    val idActividadProgramada: Int,
    val nombreActividad: String,
    val precio: Double,
    val fechaHora: String
) : Parcelable