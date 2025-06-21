package com.example.sportsclub.models

data class CarnetSocio(
    val nombreTipoDocumento: String,
    val nombre: String,
    val apellido: String,
    val documento: String,
    val nroCarnet: String,
    val imagenCarnet: ByteArray?
)