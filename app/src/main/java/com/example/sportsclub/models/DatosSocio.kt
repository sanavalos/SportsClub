package com.example.sportsclub.models

data class DatosSocio(
    val idUsuario: Int,
    val nombre: String,
    val apellido: String,
    val documento: String,
    val imagenCarnet: ByteArray?
)