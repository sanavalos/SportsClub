package com.example.sportsclub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActividadesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actividades)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val menuBack = findViewById<ImageView>(R.id.backMenu)
        menuBack.setOnClickListener {
            val intent = Intent(this, ActividadesListaActivity::class.java)
            startActivity(intent)
        }

        val siguienteButton = findViewById<Button>(R.id.siguienteButton)
        siguienteButton.setOnClickListener {
            val intent = Intent(this, DatosPagoActivity::class.java)
            startActivity(intent)
        }
    }
}