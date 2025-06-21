package com.example.sportsclub.ui

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sportsclub.R
import com.example.sportsclub.database.FormaDePagoRepository
import com.example.sportsclub.database.PagoRepository
import com.example.sportsclub.database.SocioRepository
import com.example.sportsclub.models.ActividadProgramada
import com.example.sportsclub.models.Pago
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import java.util.Date

class DatosPagoActivity : AppCompatActivity() {
    private var monto: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val pagoRepository = PagoRepository(this)
        val idUsuario = intent.getIntExtra("idUsuario", -1)
        val estadoPago = intent.getIntExtra("estadoPago", -1)
        val tipo = intent.getStringExtra("tipo")
        val actividadesIds = intent.getIntegerArrayListExtra("actividades")
        setContentView(R.layout.activity_datos_pago)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        println("ID Usuario: $idUsuario")
        println("Tipo: $tipo")
        println("Actividades IDs: $actividadesIds")

        val efectivoRadio = findViewById<RadioButton>(R.id.efectivoRadio)
        val tarjetaRadio = findViewById<RadioButton>(R.id.tarjetaRadio)

        val cuotasLayout = findViewById<TextInputLayout>(R.id.cantidad_cuotas)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.tarjetaRadio -> cuotasLayout.visibility = View.VISIBLE
                R.id.efectivoRadio -> cuotasLayout.visibility = View.GONE
            }
        }

        val menuBack = findViewById<ImageView>(R.id.backMenu)
        menuBack.setOnClickListener {
            finish()
        }

        val opcionesCuotas = listOf(
            1,
            3,
            6
        )
        val autoCompleteTextViewCuotas = findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteCuotas)
        val adapterCuotas = ArrayAdapter(this, R.layout.item_dropdown, opcionesCuotas)
        autoCompleteTextViewCuotas.setAdapter(adapterCuotas)

        autoCompleteTextViewCuotas.inputType = InputType.TYPE_NULL
        autoCompleteTextViewCuotas.keyListener = null

        autoCompleteTextViewCuotas.setOnClickListener {
            autoCompleteTextViewCuotas.showDropDown()
        }

        val formaDePagoRepository = FormaDePagoRepository(this)
        val formas = formaDePagoRepository.obtenerFormasDePago()

        formas.getOrNull(0)?.let { forma ->
            tarjetaRadio.text = forma.nombreFormaPago
            tarjetaRadio.tag = forma.idFormaPago
        }

        formas.getOrNull(1)?.let { forma ->
            efectivoRadio.text = forma.nombreFormaPago
            efectivoRadio.tag = forma.idFormaPago
        }

        val editTextMonto = findViewById<EditText>(R.id.editTextMonto)

        if (tipo == "socio") {
            val socioRepository = SocioRepository(this)
            val montoObtenido = socioRepository.obtenerMontoMensualPlanSocio(idUsuario)
            if(montoObtenido != null)
            {
                monto = montoObtenido
                editTextMonto.setText(monto.toString())
            }
        } else if (tipo == "noSocio") {
            val idsProgramadas = actividadesIds ?: emptyList()
            monto = pagoRepository.obtenerMontoTotalPorActividades(idsProgramadas)
            editTextMonto.setText(monto.toString())
        }

        val pagoButton = findViewById<Button>(R.id.pagoButton)
        pagoButton.setOnClickListener {
            val cuotasSeleccionadas = autoCompleteTextViewCuotas.text.toString().toIntOrNull() ?: 1
            val idFormaPagoSeleccionada = when {
                efectivoRadio.isChecked -> efectivoRadio.tag as? Int
                tarjetaRadio.isChecked -> tarjetaRadio.tag as? Int
                else -> null
            }

            if (idFormaPagoSeleccionada == null) {
                Toast.makeText(this, "Seleccione la forma de pago", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (cuotasSeleccionadas == null && idFormaPagoSeleccionada == 1) {
                Toast.makeText(this, "Seleccione la cantidad de cuotas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevoPago = Pago(
                idPago = 0,
                idFormaPago = idFormaPagoSeleccionada,
                idUsuario = idUsuario,
                fechaPago = Date(),
                monto = monto,
                cantCuotas = cuotasSeleccionadas
            )

            if (tipo == "socio") {
                pagoRepository.registrarPagoSocio(nuevoPago, estadoPago)
            } else if (tipo == "noSocio") {
                pagoRepository.registrarPagoNoSocio(nuevoPago, actividadesIds!!)
            }

            val intent = Intent(this, PagoOkayActivity::class.java)
            startActivity(intent)
        }
    }
}