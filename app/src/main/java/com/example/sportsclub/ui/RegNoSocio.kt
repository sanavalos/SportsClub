package com.example.sportsclub.ui

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
import com.example.sportsclub.R
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class RegNoSocio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reg_no_socio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val menuBack = findViewById<ImageView>(R.id.backMenu)
        menuBack.setOnClickListener {
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)
        }

        val noSocioOkay = findViewById<Button>(R.id.regNoSocioButton)
        noSocioOkay.setOnClickListener {
            val intent = Intent(this, NoSocioOkay::class.java)
            startActivity(intent)
        }

        //FIELD PARA TIPO DE DOCUMENTO
        val opcionesDocNoSocio = listOf(
            "DNI",
            "CI",
            "LE",
            "LC"
        )

        val autoCompleteTextViewDocNoSocio = findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteDocNoSocio)
        val adapterDocNoSocio = ArrayAdapter(this, R.layout.item_dropdown, opcionesDocNoSocio)
        autoCompleteTextViewDocNoSocio.setAdapter(adapterDocNoSocio)

        autoCompleteTextViewDocNoSocio.inputType = InputType.TYPE_NULL
        autoCompleteTextViewDocNoSocio.keyListener = null

        autoCompleteTextViewDocNoSocio.setOnClickListener {
            autoCompleteTextViewDocNoSocio.showDropDown()
        }

    } //FIN DEL ON CREATE

}