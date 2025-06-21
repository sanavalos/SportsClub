package com.example.sportsclub.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.net.Uri
import android.text.InputType
import com.example.sportsclub.R
import com.example.sportsclub.database.*
import com.example.sportsclub.models.*
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class RegSocio : AppCompatActivity() {

    private var imagenCarnetByteArray: ByteArray? = null

    //CONVERSIÓN DE UNA IMAGEN A BYTEARRAY
    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val inputStream = contentResolver.openInputStream(it)
                imagenCarnetByteArray = inputStream?.readBytes()
                inputStream?.close()

                if (imagenCarnetByteArray != null) {
                    Toast.makeText(this, "Imagen subida correctamente.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No se pudo procesar la imagen.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reg_socio)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // VOLVER AL MENÚ PRINCIPAL
        val menuBack = findViewById<ImageView>(R.id.backMenu)
        menuBack.setOnClickListener {
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)
        }

        //CAPTURA DE LA IMAGEN AL HACER CLICK EN EL BOTÓN
        val btnCarnetSocio = findViewById<Button>(R.id.btn_carnet_socio)
        btnCarnetSocio.setOnClickListener {
            filePickerLauncher.launch("image/*")
        }

        //PLANES Y TIPOS DE DOCUMENTO
        val opcionesPlan = listOf("2 veces por semana", "3 veces por semana", "Libre")
        val planSocio = findViewById<MaterialAutoCompleteTextView>(R.id.autoCompletePlan)
        planSocio.setAdapter(ArrayAdapter(this, R.layout.item_dropdown, opcionesPlan))
        planSocio.inputType = InputType.TYPE_NULL
        planSocio.setOnClickListener { planSocio.showDropDown() }

        val opcionesDoc = listOf("DNI", "Pasaporte", "LC")
        val tipoDocSocio = findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteDoc)
        tipoDocSocio.setAdapter(ArrayAdapter(this, R.layout.item_dropdown, opcionesDoc))
        tipoDocSocio.inputType = InputType.TYPE_NULL
        tipoDocSocio.setOnClickListener { tipoDocSocio.showDropDown() }

        // TOMA DE DATOS DESDE EL FORMULARIO
        val nombreSocio = findViewById<TextInputEditText>(R.id.editTextNombreSocio)
        val apellidoSocio = findViewById<TextInputEditText>(R.id.editTextApellidoSocio)
        val celularSocio = findViewById<TextInputEditText>(R.id.editTextCelularSocio)
        val domicilioSocio = findViewById<TextInputEditText>(R.id.editTextDomicilioSocio)
        val numDocSocio = findViewById<TextInputEditText>(R.id.editTextNumDocSocio)
        val checkBoxSocio = findViewById<CheckBox>(R.id.checkBox)

        val btnGuardarSocio = findViewById<Button>(R.id.regSocioButton)

        val repoTipoDoc = TipoDocumentoRepository(this)
        val repoPlan = PlanRepository(this)
        val usuarioRepo = UsuarioRepository(this)
        val socioRepo = SocioRepository(this)


        // FUNCIÓN PARA REGISTRAR SOCIO
        btnGuardarSocio.setOnClickListener {
            val nombre = nombreSocio.text.toString().trim()
            val apellido = apellidoSocio.text.toString().trim()
            val telefono = celularSocio.text.toString().trim()
            val direccion = domicilioSocio.text.toString().trim()
            val documento = numDocSocio.text.toString().trim()
            val entregoApto = checkBoxSocio.isChecked
            val tipoDoc = tipoDocSocio.text.toString()
            val tipoDocId = repoTipoDoc.obtenerIdTipoDocumento(tipoDoc)
            val planNombre = planSocio.text.toString()
            val planId = repoPlan.obtenerIdPlan(planNombre)

            if (tipoDocId == -1 || planId == -1) {
                Toast.makeText(this, "Seleccioná un tipo de documento y plan válidos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nroCarnet = "SC${System.currentTimeMillis()}"
            val fechaVencimiento = Calendar.getInstance().apply {
                add(Calendar.YEAR, 1)
            }.time

            val nuevoUsuario = Usuario(
                idTipoDocumento = tipoDocId,
                idTipoUsuario = 2,
                entregoAptoFisico = entregoApto,
                nombre = nombre,
                apellido = apellido,
                documento = documento,
                telefono = telefono,
                direccion = direccion
            )

            val idUsuario = usuarioRepo.insertarUsuario(nuevoUsuario)

            if (idUsuario != -1L) {
                val nuevoSocio = Socio(
                    idUsuario = idUsuario.toInt(),
                    idPlan = planId,
                    nroCarnet = nroCarnet,
                    fechaVencimiento = fechaVencimiento,
                    imagenCarnet = imagenCarnetByteArray
                )

                val idSocio = socioRepo.insertarSocio(nuevoSocio)

                if (idSocio != -1L) {
                    Toast.makeText(this, "Socio registrado con éxito.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SocioOkay::class.java))
                } else {
                    Toast.makeText(this, "Error al registrar socio.", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}