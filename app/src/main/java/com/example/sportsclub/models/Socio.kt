package com.example.sportsclub.models

import java.util.Date

data class Socio(
    val idUsuario: Int,
    val idPlan: Int,
    val nroCarnet: String,
    val fechaVencimiento: Date?,
    val imagenCarnet: ByteArray?
)