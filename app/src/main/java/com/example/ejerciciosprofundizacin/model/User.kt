package com.example.ejerciciosprofundizacin.model

data class User(
    val id: String,
    var name: String,
    var email: String,
    var role: UserRole,
    var status: String = "Activo",
    var phone: String = "+57 310 456 7890",
    var address: String = "Cra 15 # 85-32, Bogotá"
)
