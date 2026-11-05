package com.example.ejerciciosprofundizacin.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.ejerciciosprofundizacin.MainActivity
import com.example.ejerciciosprofundizacin.R
import com.example.ejerciciosprofundizacin.data.AppDataRepository
import com.example.ejerciciosprofundizacin.databinding.ActivityLoginBinding
import com.example.ejerciciosprofundizacin.model.UserRole
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private var selectedRole: UserRole = UserRole.BUYER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRoleSelection()
        setupBiometricAuth()

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        binding.btnBiometric.setOnClickListener {
            authenticateWithBiometrics()
        }
    }

    private fun setupRoleSelection() {
        binding.chipGroupRoles.setOnCheckedStateChangeListener { _, checkedIds ->
            when {
                checkedIds.contains(R.id.chipBuyer) -> {
                    selectedRole = UserRole.BUYER
                    binding.etEmail.setText("jpabloacevedo@ucompensar.edu.co")
                }
                checkedIds.contains(R.id.chipVendor) -> {
                    selectedRole = UserRole.VENDOR
                    binding.etEmail.setText("vendedor@irongrip.co")
                }
                checkedIds.contains(R.id.chipAdmin) -> {
                    selectedRole = UserRole.ADMIN
                    binding.etEmail.setText("admin@fitmarket.com")
                }
            }
        }
    }

    private fun setupBiometricAuth() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                // Si el dispositivo/emulador no tiene biometría configurada, permitir acceso guiado
                if (errorCode == BiometricPrompt.ERROR_NO_BIOMETRICS ||
                    errorCode == BiometricPrompt.ERROR_HW_NOT_PRESENT ||
                    errorCode == BiometricPrompt.ERROR_HW_UNAVAILABLE
                ) {
                    showBiometricFallbackDialog()
                } else {
                    Toast.makeText(applicationContext, "Autenticación cancelada: $errString", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(applicationContext, "¡Autenticación biométrica exitosa!", Toast.LENGTH_SHORT).show()
                loginSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(applicationContext, "Huella o rostro no reconocido. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("FitMarket Calisthenics")
            .setSubtitle("Autenticación biométrica de seguridad")
            .setDescription("Coloca tu huella digital o usa reconocimiento facial para ingresar rápidamente.")
            .setNegativeButtonText("Usar Contraseña")
            .build()
    }

    private fun authenticateWithBiometrics() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                showBiometricFallbackDialog()
            }
            else -> {
                showBiometricFallbackDialog()
            }
        }
    }

    private fun showBiometricFallbackDialog() {
        AlertDialog.Builder(this)
            .setTitle("Simulación Biométrica")
            .setMessage("El hardware biométrico no está enrolado en este dispositivo/emulador.\n\n¿Deseas simular la validación dactilar exitosa para el rol '${selectedRole.displayName}'?")
            .setIcon(R.drawable.ic_fingerprint)
            .setPositiveButton("Simular Huella OK") { _, _ ->
                Toast.makeText(this, "Huella verificada correctamente", Toast.LENGTH_SHORT).show()
                loginSuccess()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.tilEmail.error = "Ingresa tu correo"
            return
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = "Ingresa tu contraseña"
            return
        }
        binding.tilEmail.error = null
        binding.tilPassword.error = null

        loginSuccess()
    }

    private fun loginSuccess() {
        AppDataRepository.setRole(selectedRole)
        Toast.makeText(this, "Bienvenido: ${AppDataRepository.currentUser.name} (${selectedRole.displayName})", Toast.LENGTH_LONG).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
