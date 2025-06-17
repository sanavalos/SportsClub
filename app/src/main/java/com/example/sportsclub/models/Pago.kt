package com.example.sportsclub.models

import java.util.Date

data class Pago(
    val idPago: Int = 0,
    val idFormaPago: Int,
    val idUsuario: Int,
    val fechaPago: Date?,
    val monto: Double,
    val cantCuotas: Int?
)