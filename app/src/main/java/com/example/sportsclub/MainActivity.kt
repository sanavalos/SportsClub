package com.example.sportsclub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dbHelper = UserDBHelper (context = this)

        val pass = findViewById<TextInputLayout>(R.id.userPass)
        val user = findViewById<TextInputLayout>(R.id.usuarioEmail)
        val btnLog = findViewById<Button>(R.id.login)

        btnLog.setOnClickListener {
            val userString = user.editText?.text.toString().trim()
            val passString = pass.editText?.text.toString().trim()
            val intent = Intent(this, MainMenu::class.java)

            if(dbHelper.login(userString, passString)) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            }

            if (userString.isEmpty() || passString.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }

    }
}