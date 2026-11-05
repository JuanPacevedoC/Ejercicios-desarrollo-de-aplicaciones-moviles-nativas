package com.example.ejerciciosprofundizacin

import com.example.ejerciciosprofundizacin.data.AppDataRepository
import com.example.ejerciciosprofundizacin.model.OrderStatus
import com.example.ejerciciosprofundizacin.model.User
import com.example.ejerciciosprofundizacin.model.UserRole
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {

    @Before
    fun setup() {
        AppDataRepository.clearCart()
    }

    @Test
    fun repository_initialState_hasProductsAndUsers() {
        val products = AppDataRepository.getProducts()
        val users = AppDataRepository.getUsers()

        assertTrue("Debe contener al menos 5 productos de calistenia", products.size >= 5)
        assertTrue("Debe contener usuarios registrados", users.size >= 3)
    }

    @Test
    fun cart_operations_calculateSubtotalCorrectly() {
        val product = AppDataRepository.getProducts().first()
        AppDataRepository.addToCart(product, 2)

        val items = AppDataRepository.getCartItems()
        assertEquals(1, items.size)
        assertEquals(2, items[0].quantity)
        assertEquals(product.price * 2, AppDataRepository.getCartSubtotal(), 0.001)

        AppDataRepository.updateCartQuantity(product.id, 3)
        assertEquals(3, AppDataRepository.getCartCount())

        AppDataRepository.removeFromCart(product.id)
        assertEquals(0, AppDataRepository.getCartCount())
    }

    @Test
    fun orders_placeOrder_createsOrderAndDecrementsStock() {
        val product = AppDataRepository.getProducts().first()
        val initialStock = product.stock
        AppDataRepository.addToCart(product, 2)

        val order = AppDataRepository.placeOrder("Calle 100 # 15-20, Bogotá", "PSE")

        assertNotNull(order)
        assertTrue(order.orderNumber.startsWith("ORD-2026-"))
        assertTrue(order.radicadoPqr.startsWith("PQR-2026-"))
        assertEquals(OrderStatus.PENDIENTE, order.status)
        assertEquals(initialStock - 2, product.stock)
        assertEquals(0, AppDataRepository.getCartItems().size)
    }

    @Test
    fun user_crud_operations_workSuccessfully() {
        val newUser = User(
            id = "test_usr_99",
            name = "Usuario Prueba",
            email = "prueba@compensar.edu.co",
            role = UserRole.BUYER
        )
        AppDataRepository.addUser(newUser)
        val found = AppDataRepository.getUsers().find { it.id == "test_usr_99" }
        assertNotNull(found)

        AppDataRepository.toggleUserStatus("test_usr_99")
        assertEquals("Suspendido", found?.status)

        AppDataRepository.deleteUser("test_usr_99")
        assertNull(AppDataRepository.getUsers().find { it.id == "test_usr_99" })
    }

    @Test
    fun roleSwitcher_setsCurrentUserAndRoleCorrectly() {
        AppDataRepository.setRole(UserRole.ADMIN)
        assertEquals(UserRole.ADMIN, AppDataRepository.currentRole)
        assertEquals(UserRole.ADMIN, AppDataRepository.currentUser.role)

        AppDataRepository.setRole(UserRole.VENDOR)
        assertEquals(UserRole.VENDOR, AppDataRepository.currentRole)

        AppDataRepository.setRole(UserRole.BUYER)
        assertEquals(UserRole.BUYER, AppDataRepository.currentRole)
    }
}
