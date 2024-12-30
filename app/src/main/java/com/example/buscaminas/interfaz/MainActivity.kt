package com.example.buscaminas.interfaz

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
import com.example.buscaminas.logica.CeldaReveladaListener
import com.example.buscaminas.logica.Tablero
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private lateinit var minasRestantes: TextView
    private lateinit var tableroGrid: GridLayout
    private lateinit var reiniciarButton: AppCompatButton
    lateinit var tablero: Tablero

    val filas: Int = 14
    val columnas: Int = 8
    val minas : Int = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        minasRestantes = findViewById(R.id.minasRestantes)
        tableroGrid = findViewById(R.id.tableroGrid)
        reiniciarButton = findViewById(R.id.reiniciarButton)

        reiniciarButton.setOnClickListener {
            reiniciarJuego(filas,columnas,minas)
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
        tablero.reiniciarTablero()
        tableroGrid.removeAllViews()
        minasRestantes.text = "Minas restantes: $minas"
        Log.d("Minas", minas.toString())
        // Establece el número de filas y columnas en el GridLayout
        tableroGrid.rowCount = filas
        tableroGrid.columnCount = columnas

        // Crea un listener para calcular el tamaño de los botones dinámicamente
        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Calcula el tamaño disponible del GridLayout
                val gridWidth = tableroGrid.width
                val gridHeight = tableroGrid.height

                // Calcula el tamaño máximo para que los botones sean cuadrados y encajen en el GridLayout
                val buttonSize = Math.min(gridWidth / columnas, gridHeight / filas)

                // Agrega los botones al GridLayout
                for (fila in 0 until filas) {
                    for (columna in 0 until columnas) {
                        val botonCelda = Button(this@MainActivity)

                        // Configura cada botón con el tamaño calculado
                        configurarBotonInicial(botonCelda, fila, columna, buttonSize)
                        revelarBotonCelda(botonCelda, fila, columna,filas, columnas,minas)
                        // Agrega el botón al GridLayout
                        tableroGrid.addView(botonCelda)
                    }
                }

                // Elimina el listener para evitar llamadas repetidas
                tableroGrid.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }

        // Agrega el listener al GridLayout
        tableroGrid.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    fun configurarBotonInicial(botonCelda: Button, fila: Int, columna: Int, buttonSize: Int) {
        botonCelda.layoutParams = GridLayout.LayoutParams().apply {
            width = buttonSize
            height = buttonSize
            columnSpec = GridLayout.spec(columna)
            rowSpec = GridLayout.spec(fila)
            setMargins(0, 0, 0, 0) // Espaciado entre botones

        }
        botonCelda.setPadding(0, 0, 0, 0)
        botonCelda.text = ""
        botonCelda.setBackgroundColor(resources.getColor(R.color.white)) // Color inicial del botón
    }


    fun revelarBotonCelda(button: Button, fila: Int, columna: Int, filas: Int, columnas: Int, minas: Int) {
        val celda = tablero.tablero[fila][columna]

        button.setOnClickListener {
            if (!celda.descubierto && !celda.marcado) {
                val revelado = tablero.revelarCelda(fila, columna)
                if (revelado) {
                    actualizarTablero(filas, columnas, minas)
                    marcadorMina(minas)  // Actualiza las minas restantes después de revelar una celda
                    // Verificar si el jugador ha perdido
                    if (perdisteGame(filas, columnas)) {
                        // Muestra mensaje o realiza alguna acción si el jugador ha perdido
                        mostrarMensaje("¡Perdiste!")
                    }

                    // Verificar si el jugador ha ganado
                    if (ganasteGame(filas, columnas, minas)) {
                        // Muestra mensaje o realiza alguna acción si el jugador ha ganado
                        mostrarMensaje("¡Ganaste!")
                    }
                }
            }
        }

        button.setOnLongClickListener {
            // Cambiar el estado del botón a "banderita"
            if (button.text == "🚩") {
                celda.marcado = false
                button.text = "" // Quitar la banderita
            } else {
                celda.marcado = true
                button.text = "🚩" // Poner la banderita
            }

            // Actualizar las minas restantes cada vez que se marque o desmarque una celda
            marcadorMina(minas)
            Log.d("Marcados ",tablero.contarCeldasMarcadas().toString())

            true // Indica que el evento ha sido consumido
        }
    }

    fun actualizarTablero(filas: Int, columnas: Int, minas:Int) {


        for (fila in 0 until filas) {
            for (columna in 0 until columnas) {
                val boton = tableroGrid.getChildAt(fila * columnas + columna) as Button
                val celda = tablero.tablero[fila][columna]

                if (celda.descubierto && !celda.marcado) {
                    configuracionBotonPresionado(boton, celda)

                    if (celda.minasVecinas == 0 && !celda.esMina && !celda.marcado) {
                        boton.text = ""  // Espacio vacío si no hay minas vecinas
                    } else if (celda.esMina) {
                        boton.text = "*"  // Muestra el símbolo de mina
                    } else {
                        boton.text = celda.minasVecinas.toString()  // Número de minas vecinas
                    }

                    boton.invalidate()
                }
            }
        }
    }

    fun marcadorMina(minas: Int) {
        // Contar las minas marcadas, no solo las celdas marcadas
        val minasMarcadas = tablero.contarCeldasMarcadas()

        // Mostrar el número de minas restantes
        minasRestantes.text = "Minas restantes: ${minas - minasMarcadas}"
    }

    fun configuracionBotonPresionado(boton: Button, celda: Celda) {

        if (!celda.marcado){
            boton.isEnabled = false // Deshabilitar el botón después de revelarlo
        }else{
            boton.isEnabled = true
        }

        // Cambiar el color de fondo dependiendo de si es mina o no
        if (celda.esMina) {
            boton.setTextColor(resources.getColor(R.color.teal_700, null))
            boton.setBackgroundColor(resources.getColor(R.color.purple_200, null))
        } else {
            boton.setBackgroundColor(resources.getColor(R.color.teal_200, null))
        }

        boton.invalidate()
    }

    fun perdisteGame(filas:Int, columnas:Int):Boolean{
        for (fila in 0 until filas){
            for (columna in 0 until columnas){
                val celda = tablero.tablero[fila][columna]

                if(celda.descubierto && celda.esMina){
                    Log.d("ESTADO: ", "PERDISTE")
                    return true
                }
            }
        }
        return false
    }

    fun ganasteGame(filas:Int, columnas:Int, minas:Int):Boolean{
        var desactivados: Int =0
        for (fila in 0 until filas){
            for (columna in 0 until columnas){
                val celda = tablero.tablero[fila][columna]

                if(celda.marcado && celda.esMina && (tablero.celdasReveladas() + tablero.contarCeldasMarcadas()== filas*columnas)){
                    desactivados++
                }
            }
        }
        return desactivados == minas
    }
    fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()

    }
}
