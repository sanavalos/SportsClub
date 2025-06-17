package com.example.sportsclub.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.net.Uri
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.sportsclub.R
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class RegSocio : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reg_socio)
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

        val socioOkay = findViewById<Button>(R.id.regSocioButton)
        socioOkay.setOnClickListener {
            val intent = Intent(this, SocioOkay::class.java)
            startActivity(intent)
        }

         // CREA EL BOTÓN DE ARCHIVO ADJUNTABLE
        val btnAdjuntarArchivo = findViewById<Button>(R.id.btn_carnet_socio)
        btnAdjuntarArchivo.setOnClickListener {
            filePickerLauncher.launch("image/*")
        }

        //FIELD PARA TIPO DE PLAN
        val opcionesPlan = listOf(
            "Básico",
            "Familiar",
            "Premium",
            "Ilimitado"
        )

        val autoCompleteTextViewPlan = findViewById<MaterialAutoCompleteTextView>(R.id.autoCompletePlan)
        val adapterPlan = ArrayAdapter(this, R.layout.item_dropdown, opcionesPlan)
        autoCompleteTextViewPlan.setAdapter(adapterPlan)

        autoCompleteTextViewPlan.inputType = InputType.TYPE_NULL
        autoCompleteTextViewPlan.keyListener = null

        autoCompleteTextViewPlan.setOnClickListener {
            autoCompleteTextViewPlan.showDropDown()
        }

        //FIELD PARA TIPO DE DOCUMENTO
        val opcionesDoc = listOf(
            "DNI",
            "CI",
            "LE",
            "LC"
        )

        val autoCompleteTextViewDoc = findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteDoc)
        val adapterDoc = ArrayAdapter(this, R.layout.item_dropdown, opcionesDoc)
        autoCompleteTextViewDoc.setAdapter(adapterDoc)

        autoCompleteTextViewDoc.inputType = InputType.TYPE_NULL
        autoCompleteTextViewDoc.keyListener = null

        autoCompleteTextViewDoc.setOnClickListener {
            autoCompleteTextViewDoc.showDropDown()
        }

    } //FIN DEL ONCREATE


    //PARTE DEL BOTÓN DE ACRHIVO ADJUNTABLE
    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Toast.makeText(this, "Archivo seleccionado: $uri", Toast.LENGTH_SHORT).show()
        }
    }

    }