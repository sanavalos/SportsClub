package com.example.sportsclub

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class DatosPagoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_datos_pago)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val menuBack = findViewById<ImageView>(R.id.backMenu)
        menuBack.setOnClickListener {
            val intent = Intent(this, ActividadesActivity::class.java)
            startActivity(intent)
        }

        val pagoButton = findViewById<Button>(R.id.pagoButton)
        pagoButton.setOnClickListener {
            val intent = Intent(this, PagoOkayActivity::class.java)
            startActivity(intent)
        }

        val opcionesCuotas = listOf(
            3,
            6,
            12
        )
        val autoCompleteTextViewCuotas = findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteCuotas)
        val adapterCuotas = ArrayAdapter(this, R.layout.item_dropdown, opcionesCuotas)
        autoCompleteTextViewCuotas.setAdapter(adapterCuotas)

        autoCompleteTextViewCuotas.inputType = InputType.TYPE_NULL
        autoCompleteTextViewCuotas.keyListener = null

        autoCompleteTextViewCuotas.setOnClickListener {
            autoCompleteTextViewCuotas.showDropDown()
        }
    }
}