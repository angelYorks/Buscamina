package com.example.buscaminas.logica

class Game(val filas:Int, val columnas: Int, val numeroMinas: Int) {

    private val tablero: Tablero = Tablero(filas, columnas, numeroMinas)

    fun revelarCelda(fila: Int, columna: Int):Boolean{
        return tablero.revelarCelda(filas, columna)
    }

    fun verificarVictoria(): Boolean{
        return tablero.verificarVictoria()
    }

    fun reiniciarTablero(){
        tablero.reiniciarTablero()
    }

}