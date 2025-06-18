package com.example.sportsclub.ui

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sportsclub.R
import com.example.sportsclub.database.TipoDocumentoRepository
import com.example.sportsclub.database.UsuarioRepository
import com.example.sportsclub.models.TipoDocumento
import com.example.sportsclub.models.Usuario
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText

class RegNoSocio : AppCompatActivity() {
    private lateinit var nombreEditText: TextInputEditText
    private lateinit var apellidoEditText: TextInputEditText
    private lateinit var documentoEditText: TextInputEditText
    private lateinit var telefonoEditText: TextInputEditText
    private lateinit var direccionEditText: TextInputEditText
    private lateinit var autoCompleteTextViewDocNoSocio: MaterialAutoCompleteTextView
    private lateinit var aptoFisicoCheckBox: CheckBox

    private lateinit var opcionesDocNoSocio: List<TipoDocumento>
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

        val tipoDocumentoRepository = TipoDocumentoRepository(this)
        opcionesDocNoSocio = tipoDocumentoRepository.obtenerTiposDocumento()
        val nombresTiposDocumento = opcionesDocNoSocio.map { it.nombreTipoDocumento }



        autoCompleteTextViewDocNoSocio = findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteDocNoSocio)
        val adapterDocNoSocio = ArrayAdapter(this, R.layout.item_dropdown, nombresTiposDocumento)
        autoCompleteTextViewDocNoSocio.setAdapter(adapterDocNoSocio)

        autoCompleteTextViewDocNoSocio.inputType = InputType.TYPE_NULL
        autoCompleteTextViewDocNoSocio.keyListener = null

        autoCompleteTextViewDocNoSocio.setOnClickListener {
            autoCompleteTextViewDocNoSocio.showDropDown()
        }

        nombreEditText = findViewById<TextInputEditText>(R.id.editTextNombre)
        apellidoEditText = findViewById<TextInputEditText>(R.id.editTextApellido)
        telefonoEditText = findViewById<TextInputEditText>(R.id.editTextTelefono)
        direccionEditText = findViewById<TextInputEditText>(R.id.editTextDireccion)
        documentoEditText = findViewById<TextInputEditText>(R.id.editTextDocumento)
        aptoFisicoCheckBox = findViewById<CheckBox>(R.id.checkBoxNoSocio)

        val noSocioOkay = findViewById<Button>(R.id.regNoSocioButton)
        noSocioOkay.setOnClickListener {
            val usuario = validarFormularioNoSocio()

            if(usuario != null)
            {
                val usuarioRepository = UsuarioRepository(this)
                val success = usuarioRepository.crearNoSocio(usuario)

                if(success)
                {
                    val intent = Intent(this, NoSocioOkay::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "No Socio registrado correctamente", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(this, "Error al registrar el No Socio", Toast.LENGTH_SHORT).show()
                }
            }




        }
    } //FIN DEL ON CREATE

    private fun validarFormularioNoSocio(): Usuario? {
        val nombre = nombreEditText.text.toString().trim()
        val apellido = apellidoEditText.text.toString().trim()
        val documento = documentoEditText.text.toString().trim()
        val telefono = telefonoEditText.text.toString().trim()
        val direccion = direccionEditText.text.toString().trim()
        val aptoFisico = aptoFisicoCheckBox.isChecked
        val nombreDocSeleccionado = autoCompleteTextViewDocNoSocio.text.toString().trim()

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingresa el nombre", Toast.LENGTH_SHORT).show()
            return null
        }

        if (apellido.isEmpty()) {
            Toast.makeText(this, "Ingresa el apellido", Toast.LENGTH_SHORT).show()
            return null
        }

        if (telefono.isEmpty()) {
            Toast.makeText(this, "Ingresa el teléfono", Toast.LENGTH_SHORT).show()
            return null
        }

        if (direccion.isEmpty()) {
            Toast.makeText(this, "Ingresa la dirección", Toast.LENGTH_SHORT).show()
            return null
        }

        if (documento.isEmpty()) {
            Toast.makeText(this, "Ingresa el número de documento", Toast.LENGTH_SHORT).show()
            return null
        }

        if (nombreDocSeleccionado.isEmpty()) {
            Toast.makeText(this, "Seleccioná un tipo de documento", Toast.LENGTH_SHORT).show()
            return null
        }

        val tipoDocSeleccionado = opcionesDocNoSocio.find { it.nombreTipoDocumento == nombreDocSeleccionado }

        if (tipoDocSeleccionado == null) {
            Toast.makeText(this, "Tipo de documento inválido", Toast.LENGTH_SHORT).show()
            return null
        }

        return Usuario(
            idTipoDocumento = tipoDocSeleccionado.idTipoDocumento,
            idTipoUsuario = 3,
            entregoAptoFisico = aptoFisico,
            nombre = nombre,
            apellido = apellido,
            documento = documento,
            telefono = telefono,
            direccion = direccion
        )
    }
}