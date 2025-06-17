package com.example.sportsclub.models

data class Usuario(
    val idUsuario: Int = 0,
    val idTipoDocumento: Int,
    val idTipoUsuario: Int,
    val entregoAptoFisico: Boolean?,
    val nombre: String,
    val apellido: String,
    val documento: String,
    val telefono: String,
    val direccion: String
)