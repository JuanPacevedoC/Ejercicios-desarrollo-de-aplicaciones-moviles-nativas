package com.example.ejerciciosprofundizacin.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ejerciciosprofundizacin.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val auth = FirebaseAuth.getInstance()

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

            binding.btnSendReset.isEnabled = false
            binding.btnSendReset.text = "Enviando..."

            // Restablecimiento real con Firebase Auth
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    binding.btnSendReset.isEnabled = true
                    binding.btnSendReset.text = "Enviar Enlace de Restablecimiento"

                    val message = if (task.isSuccessful) {
                        "Hemos enviado un enlace seguro a $email para restablecer tu contraseña.\n\nPor favor revisa tu bandeja de entrada o carpeta de spam."
                    } else {
                        "Instrucciones de recuperación tramitadas para $email.\n(Si no recibes el correo en pocos minutos, verifica que el correo esté registrado)."
                    }

                    AlertDialog.Builder(this)
                        .setTitle("Recuperación de Contraseña")
                        .setMessage(message)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("Entendido") { _, _ ->
                            finish()
                        }
                        .setCancelable(false)
                        .show()
                }
        }
    }
}
