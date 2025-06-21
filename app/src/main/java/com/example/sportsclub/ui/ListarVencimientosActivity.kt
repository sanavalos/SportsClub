package com.example.sportsclub.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageView
import android.graphics.Bitmap
import android.graphics.Canvas
import android.content.ContentValues
import android.provider.MediaStore
import android.os.Build
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsclub.R
import com.example.sportsclub.adapters.ListarVencimientosAdapter
import com.example.sportsclub.database.SocioRepository

class ListarVencimientosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listar_vencimientos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backMenu = findViewById<ImageView>(R.id.backMenu)

        backMenu.setOnClickListener {
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerVencimientos)
        val labelInformativo = findViewById<TextView>(R.id.labelInformativo)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val socioRepository = SocioRepository(this)
        val socios = socioRepository.obtenerSociosConVencimientoHoy()

        if(socios.size > 0)
        {
            recyclerView.visibility = View.VISIBLE
            labelInformativo.visibility = View.GONE
        }
        else
        {
            recyclerView.visibility = View.GONE
            labelInformativo.visibility = View.VISIBLE
        }

        val adapter = ListarVencimientosAdapter(socios)
        recyclerView.adapter = adapter

        val btnImprimir = findViewById<View>(R.id.Imprimirpdf)
        btnImprimir.setOnClickListener {
            val recyclerView = findViewById<RecyclerView>(R.id.recyclerVencimientos)
            val bitmap = capturarRecyclerView(recyclerView)
            val guardado = guardarEnGaleria(bitmap)

            if (guardado) {
                Toast.makeText(this, "Imagen guardada en Galería", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Error al guardar imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun capturarRecyclerView(recyclerView: RecyclerView): Bitmap {
        val adapter = recyclerView.adapter ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val itemCount = adapter.itemCount
        val viewHolders = mutableListOf<View>()
        var totalHeight = 0

        val width = recyclerView.width

        for (i in 0 until itemCount) {
            val holder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i))
            adapter.onBindViewHolder(holder, i)

            val view = holder.itemView
            view.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)

            viewHolders.add(view)
            totalHeight += view.measuredHeight
        }

        val bitmap = Bitmap.createBitmap(width, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        var y = 0
        for (view in viewHolders) {
            view.draw(canvas)
            canvas.translate(0f, view.height.toFloat())
            y += view.height
        }

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