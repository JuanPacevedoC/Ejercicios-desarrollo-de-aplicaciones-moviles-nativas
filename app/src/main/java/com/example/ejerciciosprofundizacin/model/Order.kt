package com.example.ejerciciosprofundizacin.model

enum class OrderStatus(val label: String) {
    PENDIENTE("Pendiente"),
    PREPARACION("En preparación"),
    ENVIADO("Enviado"),
    ENTREGADO("Entregado")
}

data class Order(
    val id: String,
    val orderNumber: String,
    val date: String,
    val buyerName: String,
    val items: List<CartItem>,
    val total: Double,
    var status: OrderStatus,
    val shippingAddress: String,
    val paymentMethod: String,
    val radicadoPqr: String
)
