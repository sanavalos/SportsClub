package com.example.sportsclub.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sportsclub.R
import com.example.sportsclub.database.LoginRepository
import com.example.sportsclub.database.UserDBHelper
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

        val dbHelper = UserDBHelper(this)
        dbHelper.writableDatabase

//FUNCIÓN LOGIN
        val repo = LoginRepository(this)
        val pass = findViewById<TextInputLayout>(R.id.userPass)
        val user = findViewById<TextInputLayout>(R.id.userEmail)
        val btnLog = findViewById<Button>(R.id.login)

        btnLog.setOnClickListener {
            val userString = user.editText?.text.toString().trim()
            val passString = pass.editText?.text.toString().trim()

            if (userString.isEmpty() || passString.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (repo.login(userString, passString)) {
                val intent = Intent(this, MainMenu::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Usuario no encontrado.", Toast.LENGTH_SHORT).show()
            }
        }


    }
}