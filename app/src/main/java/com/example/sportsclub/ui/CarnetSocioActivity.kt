package com.example.sportsclub.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sportsclub.R
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Bitmap
import android.content.ContentValues
import android.provider.MediaStore
import android.os.Build
import android.view.View
import android.widget.Toast
import com.example.sportsclub.database.SocioRepository

class CarnetSocioActivity : AppCompatActivity() {
    private lateinit var searchInput: EditText
    private lateinit var searchContainer: RelativeLayout
    private lateinit var searchIcon: ImageView
    private lateinit var toolbar: Toolbar
    private lateinit var contenedorVerde: LinearLayout
    private lateinit var botones: LinearLayout
    private lateinit var imprimir: Button
    private lateinit var volver: Button

    private lateinit var textNombre: TextView
    private lateinit var textDNI: TextView
    private lateinit var textSocio: TextView
    private lateinit var imageCarnet: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_carnet_socio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchInput = findViewById<EditText>(R.id.searchInput)
        searchIcon = findViewById<ImageView>(R.id.searchIcon)
        searchContainer = findViewById<RelativeLayout>(R.id.searchContainer)
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        contenedorVerde = findViewById<LinearLayout>(R.id.contenedorVerde)
        botones = findViewById<LinearLayout>(R.id.botones)
        imprimir = findViewById<Button>(R.id.ImprimirCarnet)
        volver = findViewById<Button>(R.id.volver)

        textNombre = findViewById<TextView>(R.id.textNombre)
        textDNI = findViewById<TextView>(R.id.textDNI)
        textSocio = findViewById<TextView>(R.id.textSocio)
        imageCarnet = findViewById<ImageView>(R.id.imageView3)

        val socioRepo = SocioRepository(this)

        searchIcon.setOnClickListener {
            val documento = searchInput.text.toString().trim()

            if (documento.isNotEmpty()) {
                val datos = socioRepo.obtenerDatosSocioPorDocumento(documento)

                if (datos != null) {
                    textNombre.text = "${datos.nombre} ${datos.apellido}"
                    textDNI.text = "${datos.nombreTipoDocumento}: ${formatearDNI(datos.documento)}"
                    textSocio.text = datos.nroCarnet

                    datos.imagenCarnet?.let {
                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        imageCarnet.setImageBitmap(bitmap)
                    }

                    searchInput.setText("")
                    contenedorVerde.visibility = View.VISIBLE
                    botones.visibility = View.VISIBLE
                    searchContainer.visibility = View.GONE
                    toolbar.visibility = View.GONE

                } else {
                    searchInput.setText("")
                    Toast.makeText(this, "No se encontró ningún socio con ese número de documento", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Ingrese un documento", Toast.LENGTH_SHORT).show()
            }
        }

        val menuBack = findViewById<ImageView>(R.id.backMenu)
        menuBack.setOnClickListener {
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)
        }

        volver.setOnClickListener {
            contenedorVerde.visibility = View.GONE
            botones.visibility = View.GONE
            searchContainer.visibility = View.VISIBLE
            toolbar.visibility = View.VISIBLE
        }

        imprimir.setOnClickListener {
            val bitmap = capturarVista(contenedorVerde)
            val guardado = guardarEnGaleria(bitmap)

            if (guardado) {
                Toast.makeText(this, "Imagen guardada en Galería", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Error al guardar imagen", Toast.LENGTH_SHORT).show()
            }

            contenedorVerde.visibility = View.GONE
            botones.visibility = View.GONE
            searchContainer.visibility = View.VISIBLE
            toolbar.visibility = View.VISIBLE
        }

    }

    private fun formatearDNI(dni: String): String {
        return dni.reversed()
            .chunked(3)
            .joinToString(".")
            .reversed()
    }

    private fun capturarVista(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun guardarEnGaleria(bitmap: Bitmap): Boolean {
        val resolver = contentResolver
        val nombreArchivo = "vencimientos_${System.currentTimeMillis()}.png"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, nombreArchivo)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.WIDTH, bitmap.width)
            put(MediaStore.Images.Media.HEIGHT, bitmap.height)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Vencimientos")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it).use { outputStream ->
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.flush()
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }

            return true
        }

        return false
    }
}