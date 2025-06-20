package com.example.sportsclub.models

data class UsuarioPago (
    val idUsuario: Int,
    val nombre: String,
    val apellido: String,
    val documento: String,
    val idTipoUsuario: Int,
    val nombreTipoUsuario: String,
    val pagos: List<Pago>
)