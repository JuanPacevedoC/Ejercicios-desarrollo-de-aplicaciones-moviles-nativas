package com.example.ejerciciosprofundizacin.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.ejerciciosprofundizacin.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navegar a Login después de 1.8 segundos o al tocar la pantalla
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable { navigateToLogin() }
        handler.postDelayed(runnable, 1800)

        binding.splashRoot.setOnClickListener {
            handler.removeCallbacks(runnable)
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
