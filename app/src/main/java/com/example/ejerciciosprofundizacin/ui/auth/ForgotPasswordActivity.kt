package com.example.ejerciciosprofundizacin.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ejerciciosprofundizacin.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.tvBackToLogin.setOnClickListener {
            finish()
        }

        binding.btnSendReset.setOnClickListener {
            val email = binding.etForgotEmail.text.toString().trim()
            if (email.isEmpty() || !email.contains("@")) {
                binding.tilForgotEmail.error = "Ingresa un correo electrónico válido"
                return@setOnClickListener
            }
            binding.tilForgotEmail.error = null

            AlertDialog.Builder(this)
                .setTitle("Enlace Enviado")
                .setMessage("Hemos enviado un correo de recuperación a $email con las instrucciones para restablecer tu contraseña.\n\nPor favor revisa tu bandeja de entrada y la carpeta de spam.")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Entendido") { _, _ ->
                    Toast.makeText(this, "Instrucciones enviadas con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .setCancelable(false)
                .show()
        }
    }
}
