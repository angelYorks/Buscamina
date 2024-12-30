package com.example.buscaminas.interfaz

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.buscaminas.R
import com.example.buscaminas.logica.Celda
import com.example.buscaminas.logica.Game
import com.example.buscaminas.logica.Tablero
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private lateinit var minasRestantes: TextView
    private lateinit var tableroGrid: GridLayout
    private lateinit var reiniciarButton: AppCompatButton
    private lateinit var game: Game

    private val filas: Int = 14
    private val columnas: Int = 8
    private val minas: Int = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        minasRestantes = findViewById(R.id.minasRestantes)
        tableroGrid = findViewById(R.id.tableroGrid)
        reiniciarButton = findViewById(R.id.reiniciarButton)

        reiniciarButton.setOnClickListener {
            reiniciarJuego()
        }

        game = Game(filas, columnas, minas)
        reiniciarJuego()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun reiniciarJuego() {
        game.reiniciarJuego()
        tableroGrid.removeAllViews()
        minasRestantes.text = "Minas restantes: $minas"
        Log.d("Minas", minas.toString())
        tableroGrid.rowCount = filas
        tableroGrid.columnCount = columnas

        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val gridWidth = tableroGrid.width
                val gridHeight = tableroGrid.height
                val buttonSize = Math.min(gridWidth / columnas, gridHeight / filas)

                for (fila in 0 until filas) {
                    for (columna in 0 until columnas) {
                        val botonCelda = Button(this@MainActivity)
                        configurarBotonInicial(botonCelda, fila, columna, buttonSize)
                        revelarBotonCelda(botonCelda, fila, columna)
                        tableroGrid.addView(botonCelda)
                    }
                }

                tableroGrid.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }

        tableroGrid.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    private fun configurarBotonInicial(botonCelda: Button, fila: Int, columna: Int, buttonSize: Int) {
        botonCelda.layoutParams = GridLayout.LayoutParams().apply {
            width = buttonSize
            height = buttonSize
            columnSpec = GridLayout.spec(columna)
            rowSpec = GridLayout.spec(fila)
            setMargins(0, 0, 0, 0)
        }
        botonCelda.setPadding(0, 0, 0, 0)
        botonCelda.text = ""
        botonCelda.setTextColor(Color.parseColor("#37474F"))
        botonCelda.setBackgroundResource(R.drawable.selector_fondo_boton) // Fondo personalizado
    }

    private fun revelarBotonCelda(button: Button, fila: Int, columna: Int) {
        val celda = game.tablero.tablero[fila][columna]

        button.setOnClickListener {
            if (!celda.descubierto && !celda.marcado) {
                val revelado = game.revelarCelda(fila, columna)
                if (revelado) {
                    actualizarTablero()
                    marcadorMina()
                    if (game.perdisteGame()) {
                        game.reiniciarJuego()
                        mostrarMensaje("¡Perdiste!")
                    }
                    if (game.ganasteGame()) {
                        game.reiniciarJuego()
                        mostrarMensaje("¡Ganaste!")
                    }
                }
            }
        }

        button.setOnLongClickListener {
            if (button.text == "🚩") {
                celda.marcado = false
                button.text = ""
            } else {
                celda.marcado = true
                button.text = "🚩"
                if (game.ganasteGame()) {
                    game.reiniciarJuego()
                    mostrarMensaje("¡Ganaste!")
                }
            }
            marcadorMina()
            true
        }
    }

    private fun actualizarTablero() {
        for (fila in 0 until filas) {
            for (columna in 0 until columnas) {
                val boton = tableroGrid.getChildAt(fila * columnas + columna) as Button
                val celda = game.tablero.tablero[fila][columna]

                if (celda.descubierto && !celda.marcado) {
                    configuracionBotonPresionado(boton, celda)

                    boton.text = when {
                        celda.minasVecinas == 0 && !celda.esMina && !celda.marcado -> ""
                        celda.esMina -> "*"
                        else -> celda.minasVecinas.toString()
                    }

                    boton.invalidate()
                }
            }
        }
    }

    private fun marcadorMina() {
        val minasMarcadas = game.contarCeldasMarcadas()
        minasRestantes.text = "Minas restantes: ${minas - minasMarcadas}"
    }

    private fun configuracionBotonPresionado(boton: Button, celda: Celda) {
        boton.isEnabled = !celda.marcado
        if (celda.esMina) {
            boton.setBackgroundResource(R.drawable.button_pressed_background)
        } else {
            boton.setBackgroundResource(R.drawable.button_pressed_background)
        }

        boton.invalidate()
    }


    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}
