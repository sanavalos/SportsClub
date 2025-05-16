package com.example.sportsclub

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val menuButton = findViewById<ImageView>(R.id.botonHamburguesa)

        menuButton.setOnClickListener {
            val bottomSheetView = layoutInflater.inflate(R.layout.burger_menu, null)
            val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
            dialog.setContentView(bottomSheetView)

            bottomSheetView.viewTreeObserver.addOnGlobalLayoutListener {
                val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
                val behavior = BottomSheetBehavior.from(bottomSheet!!)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            dialog.show()
        }

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
    }

}