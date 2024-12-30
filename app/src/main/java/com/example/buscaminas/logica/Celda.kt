package com.example.buscaminas.logica

data class Celda(
    var esMina:Boolean = false,
    var descubierto:Boolean = false,
    var marcado: Boolean = false,
    var minasVecinas: Int = 0
)
