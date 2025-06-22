package com.example.sportsclub.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.sportsclub.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainMenu : AppCompatActivity() {

    private lateinit var menuButton: ImageView
    private lateinit var dialog: BottomSheetDialog
    private var bottomSheetView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu)

        menuButton = findViewById(R.id.botonHamburguesa)

        val buttonSocio = findViewById<Button>(R.id.regSocio)
        buttonSocio.setOnClickListener {
            val intent = Intent(this, RegSocio::class.java)
            startActivity(intent)
        }

        val buttonNoSocio = findViewById<Button>(R.id.regNoSocio)
        buttonNoSocio.setOnClickListener {
            val intent = Intent(this, RegNoSocio::class.java)
            startActivity(intent)
        }

        bottomSheetView = layoutInflater.inflate(R.layout.burger_menu, null)
        dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        dialog.setContentView(bottomSheetView!!)

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        dialog.setOnDismissListener {
            menuButton.setImageResource(R.drawable.burger)
        }

        menuButton.setOnClickListener {
            if (dialog.isShowing) {
                dialog.dismiss()
            } else {
                dialog.show()
                menuButton.setImageResource(R.drawable.icon_close)
            }
        }

        bottomSheetView?.apply {
            findViewById<TextView>(R.id.listaPagosButton).setOnClickListener {
                startActivity(Intent(this@MainMenu, PaymentListActivity::class.java))
                dialog.dismiss()
            }
            findViewById<TextView>(R.id.listarVencimientosButton).setOnClickListener {
                startActivity(Intent(this@MainMenu, ListarVencimientosActivity::class.java))
                dialog.dismiss()
            }
            findViewById<TextView>(R.id.imprimirCarnetButton).setOnClickListener {
                startActivity(Intent(this@MainMenu, CarnetSocioActivity::class.java))
                dialog.dismiss()
            }
            findViewById<TextView>(R.id.asignarActividadButton).setOnClickListener {
                startActivity(Intent(this@MainMenu, ActividadesListaActivity::class.java))
                dialog.dismiss()
            }
            findViewById<TextView>(R.id.registrarSocioButton).setOnClickListener {
                startActivity(Intent(this@MainMenu, RegSocio::class.java))
            }
            findViewById<TextView>(R.id.registrarNoSocioButton).setOnClickListener {
                startActivity(Intent(this@MainMenu, RegNoSocio::class.java))
            }
        }
    }
}