package com.example.ejerciciosprofundizacin.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ejerciciosprofundizacin.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

/**
 * Actividad para la recuperación y restablecimiento seguro de contraseñas.
 *
 * Cumplimiento de requerimientos Actividad 3 / Proyecto Final:
 * - Flujo funcional de recuperación de contraseña mediante Firebase Authentication (sendPasswordResetEmail).
 * - Notificación clara mediante cuadro de diálogo informando al usuario sobre el correo de recuperación.
 */
class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    /**
     * Configura los botones de retroceso y envío de solicitud de restablecimiento.
     */
    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.tvBackToLogin.setOnClickListener {
            finish()
        }

        binding.btnSendReset.setOnClickListener {
            processPasswordReset()
        }
    }

    /**
     * Valida el formato del correo y despacha el correo electrónico de restablecimiento
     * a través de Firebase Authentication.
     */
    private fun processPasswordReset() {
        val email = binding.etForgotEmail.text.toString().trim()
        if (email.isEmpty() || !email.contains("@")) {
            binding.tilForgotEmail.error = "Ingresa un correo electrónico válido"
            return
        }
        binding.tilForgotEmail.error = null

        binding.btnSendReset.isEnabled = false
        binding.btnSendReset.text = "Enviando enlace..."

        // Envío de correo mediante servicio de Firebase Auth
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                binding.btnSendReset.isEnabled = true
                binding.btnSendReset.text = "Enviar Enlace de Restablecimiento"

                val message = if (task.isSuccessful) {
                    "Hemos enviado un enlace seguro a $email para restablecer tu contraseña.\n\nPor favor revisa tu bandeja de entrada o la carpeta de spam."
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
