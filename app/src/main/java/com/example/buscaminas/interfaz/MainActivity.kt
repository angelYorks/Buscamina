package com.example.buscaminas.interfaz

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.buscaminas.R
import com.example.buscaminas.logica.Celda
import com.example.buscaminas.logica.CeldaReveladaListener
import com.example.buscaminas.logica.Tablero
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private lateinit var minasRestantes: TextView
    private lateinit var tableroGrid: GridLayout
    private lateinit var reiniciarButton: AppCompatButton
    lateinit var tablero: Tablero

    val filas: Int = 12
    val columnas: Int = 7
    val minas : Int = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        minasRestantes = findViewById(R.id.minasRestantes)
        tableroGrid = findViewById(R.id.tableroGrid)
        reiniciarButton = findViewById(R.id.reiniciarButton)

        reiniciarButton.setOnClickListener {
            reiniciarJuego(filas,columnas,columnas)
        }

        reiniciarJuego(filas,columnas, minas)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun reiniciarJuego(filas: Int, columnas: Int, minas: Int) {
        // Inicializa el tablero y limpia el GridLayout
        tablero = Tablero(filas, columnas, minas)
        tableroGrid.removeAllViews()

        // Establece el número de filas y columnas en el GridLayout
        tableroGrid.rowCount = filas
        tableroGrid.columnCount = columnas

        // Calcula el tamaño de los botones de manera dinámica en función del tamaño de la pantalla
        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        // Calcular el tamaño proporcional de los botones
        val buttonSize = Math.min(width / columnas, height / filas)

        for (fila in 0 until filas) {
            for (columna in 0 until columnas) {
                val botonCelda = Button(this)

                // Configura cada botón con el tamaño calculado y su posición
                configurarBotonInicial(botonCelda, fila, columna, buttonSize, filas, columnas)

                // Agrega el botón al GridLayout
                tableroGrid.addView(botonCelda)
            }
        }
    }
    fun configurarBotonInicial(botonCelda: Button, fila: Int, columna: Int, buttonSize: Int, filas: Int, columnas: Int) {
        // Configuración de tamaño y márgenes
        botonCelda.layoutParams = GridLayout.LayoutParams().apply {
            width = buttonSize
            height = buttonSize
            columnSpec = GridLayout.spec(columna)
            rowSpec = GridLayout.spec(fila)
            setMargins(0, 0, 0, 0)
        }

        botonCelda.text = ""

        revelarBotonCelda(botonCelda, fila, columna, filas, columnas)
    }


    private fun revelarBotonCelda(button: Button, fila: Int, columna: Int, filas: Int, columnas: Int) {

        val celda = tablero.tablero[fila][columna]

        button.setOnClickListener {
            if(!celda.descubierto){
                val revelado = tablero.revelarCelda(fila, columna)
                if (revelado) {
                    actualizarTablero(filas, columnas)
                }
            }

        }
    }


    fun actualizarTablero(filas: Int, columnas: Int) {
        for (fila in 0 until filas) {
            for (columna in 0 until columnas) {
                val boton = tableroGrid.getChildAt(fila * columnas + columna) as Button
                val celda = tablero.tablero[fila][columna]

                if (celda.descubierto) {
                    configuracionBotonPresionado(boton, celda)

                    if (celda.minasVecinas == 0 && !celda.esMina) {
                        boton.text = ""  // Espacio vacío si no hay minas vecinas
                    } else if (celda.esMina) {
                        marcadorMina()
                        boton.text = "*"  // Muestra el símbolo de mina
                    } else {
                        boton.text = celda.minasVecinas.toString()  // Número de minas vecinas
                    }

                    boton.invalidate()
                }
            }
        }
    }

    fun marcadorMina(){
        minasRestantes.text = "Minas restantes: "+tablero.contarMinasReveladas().toString()
    }

    fun configuracionBotonPresionado(boton: Button, celda: Celda) {
        boton.isEnabled = false  // Deshabilitar el botón después de revelarlo

        // Cambiar el color de fondo dependiendo de si es mina o no
        if (celda.esMina) {
            boton.setTextColor(resources.getColor(R.color.teal_700, null))
            boton.setBackgroundColor(resources.getColor(R.color.purple_200, null))
        } else {
            boton.setBackgroundColor(resources.getColor(R.color.teal_200, null))
        }

        boton.invalidate()
    }

}
