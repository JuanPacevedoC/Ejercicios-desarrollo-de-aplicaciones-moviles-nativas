package com.example.ejerciciosprofundizacin.model

data class Product(
    val id: String,
    var name: String,
    var category: String,
    var price: Double,
    var stock: Int,
    var description: String,
    var material: String,
    var dimensions: String,
    var weightCapacity: String,
    var sellerName: String,
    var iconRes: Int = com.example.ejerciciosprofundizacin.R.drawable.ic_fitness_center
)
