package com.example.buscaminas.logica

import android.util.Log
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import com.example.buscaminas.R


class Game(private val filas: Int, private val columnas: Int, private val minas: Int) {

    val tablero: Tablero = Tablero(filas, columnas, minas)

    fun reiniciarJuego() {
        tablero.reiniciarTablero()
    }

    fun revelarCelda(fila: Int, columna: Int): Boolean {
        return tablero.revelarCelda(fila, columna)
    }

    fun contarCeldasMarcadas(): Int {
        return tablero.contarCeldasMarcadas()
    }

    fun celdasReveladas(): Int {
        return tablero.celdasReveladas()
    }

    fun perdisteGame(): Boolean {
        for (fila in 0 until filas) {
            for (columna in 0 until columnas) {
                val celda = tablero.tablero[fila][columna]
                if (celda.descubierto && celda.esMina) {
                    Log.d("ESTADO: ", "PERDISTE")
                    return true
                }
            }
        }
        return false
    }

    fun ganasteGame(): Boolean {
        var desactivados = 0
        for (fila in 0 until filas) {
            for (columna in 0 until columnas) {
                val celda = tablero.tablero[fila][columna]
                if (celda.marcado && celda.esMina &&
                    (tablero.celdasReveladas() + tablero.contarCeldasMarcadas() == filas * columnas)
                ) {
                    desactivados++
                }
            }
        }
        return desactivados == minas
    }
}