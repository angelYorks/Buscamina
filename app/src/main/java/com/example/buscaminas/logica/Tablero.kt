package com.example.buscaminas.logica

import android.util.Log


class Tablero(val filas: Int, val columnas: Int, val numeroMinas: Int ) {

    var tablero: Array<Array<Celda>> = Array(filas) { Array(columnas) { Celda() } }

    var listener: CeldaReveladaListener? = null

    fun setCeldaReveladaListener(listener: CeldaReveladaListener) {
        this.listener = listener
    }

    init {
        colocarMinas()
        calcularMinasVecinas()
    }

    private fun colocarMinas() {
        var minasColocadas = 0
        while (minasColocadas < numeroMinas) {
            val fila = (0 until filas).random()
            val columna = (0 until columnas).random()
            if (!tablero[fila][columna].esMina) {
                tablero[fila][columna].esMina = true
                minasColocadas++
            }
        }
    }

    private fun calcularMinasVecinas() {
        for (fila in 0 until filas) {
            for (columna in 0 until columnas) {
                if (tablero[fila][columna].esMina) continue
                tablero[fila][columna].minasVecinas = contarMinasVecinas(fila, columna)
            }
        }
    }

    private fun contarMinasVecinas(fila: Int, columna: Int): Int {
        var contador = 0
        for (i in -1..1) {
            for (j in -1..1) {
                val nuevaFila = fila + i
                val nuevaColumna = columna + j
                if (nuevaFila in 0 until filas && nuevaColumna in 0 until columnas) {
                    if (tablero[nuevaFila][nuevaColumna].esMina) {
                        contador++
                    }
                }
            }
        }
        return contador
    }

    fun revelarCelda(fila: Int, columna: Int): Boolean {
        val celda = tablero[fila][columna]

        if (celda.descubierto) return false

        celda.descubierto = true
        Log.d("Mensaje", "Revelando celda: ($fila, $columna)")

        listener?.onCeldaRevelada(fila, columna)

        if (celda.minasVecinas == 0) {
            for (i in -1..1) {
                for (j in -1..1) {
                    val nuevaFila = fila + i
                    val nuevaColumna = columna + j
                    if (nuevaFila in 0 until filas && nuevaColumna in 0 until columnas) {
                        val vecina = tablero[nuevaFila][nuevaColumna]
                        if (!vecina.descubierto) {
                            Log.d("Mensaje", "Revelando vecina: ($nuevaFila, $nuevaColumna)")
                            revelarCelda(nuevaFila, nuevaColumna)  // Recursión
                        }
                    }
                }
            }
        }

        return true
    }

    fun celdasReveladas():Int{
        var contador = 0
        for (fila in 0 until filas){
            for (columna in 0 until columnas){
                if(tablero[fila][columna].descubierto){
                    contador++
                }
            }
        }
        return contador
    }

    fun verificarVictoria(): Boolean {
        for (fila in 0 until filas) {
            for (columna in 0 until columnas) {
                if (!tablero[fila][columna].esMina && !tablero[fila][columna].descubierto) {
                    return false
                }
            }
        }
        return true
    }


    fun reiniciarTablero() {
        limpiarTablero()
        colocarMinas()
        calcularMinasVecinas()
    }

    private fun limpiarTablero() {
        for (fila in 0 until filas) {
            for (columna in 0 until columnas) {
                val nuevaCelda = Celda()
                tablero[fila][columna] = nuevaCelda
            }
        }
    }

    fun contarMinasReveladas(): Int {
        var contador = 0
        for (fila in 0 until filas) {
            for(columna in 0 until columnas){
                if(tablero[fila][columna].esMina && !tablero[fila][columna].descubierto)
                    contador++
            }
        }
        return contador

    }

}