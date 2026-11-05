package com.example.ejerciciosprofundizacin.data

import com.example.ejerciciosprofundizacin.R
import com.example.ejerciciosprofundizacin.model.CartItem
import com.example.ejerciciosprofundizacin.model.Order
import com.example.ejerciciosprofundizacin.model.OrderStatus
import com.example.ejerciciosprofundizacin.model.Product
import com.example.ejerciciosprofundizacin.model.User
import com.example.ejerciciosprofundizacin.model.UserRole
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object AppDataRepository {

    var currentRole: UserRole = UserRole.BUYER
    var currentUser: User = User(
        id = "usr_003",
        name = "Juan Pablo Acevedo",
        email = "jpabloacevedo@ucompensar.edu.co",
        role = UserRole.BUYER,
        status = "Activo",
        phone = "+57 312 987 6543",
        address = "Calle 72 # 11-45, Chapinero, Bogotá"
    )

    private val usersList = mutableListOf(
        User("usr_001", "Administrador Principal", "admin@fitmarket.com", UserRole.ADMIN, "Activo", "+57 300 123 4567", "Sede Central Compensar, Bogotá"),
        User("usr_002", "IronGrip Calisthenics", "vendedor@irongrip.co", UserRole.VENDOR, "Activo", "+57 311 555 8899", "Zona Industrial Calle 13 # 68-10, Bogotá"),
        User("usr_003", "Juan Pablo Acevedo", "jpabloacevedo@ucompensar.edu.co", UserRole.BUYER, "Activo", "+57 312 987 6543", "Calle 72 # 11-45, Chapinero, Bogotá"),
        User("usr_004", "Apex Movement Store", "contacto@apexmovement.com", UserRole.VENDOR, "Activo", "+57 315 222 3344", "Cra 43A # 1-50, Medellín"),
        User("usr_005", "Carolina Morales", "carolina.m@gmail.com", UserRole.BUYER, "Activo", "+57 318 444 1122", "Av. Circunvalar # 4-80, Cali"),
        User("usr_006", "David Calistenia Pro", "david.pro@outlook.com", UserRole.BUYER, "Suspendido", "+57 301 777 9900", "Carrera 53 # 75-12, Barranquilla")
    )

    private val productsList = mutableListOf(
        Product(
            id = "prod_001",
            name = "Barra de Dominadas Multifuncional Pared",
            category = "Barras",
            price = 185000.0,
            stock = 15,
            description = "Barra fija de alta resistencia para anclaje a muro de concreto. Incluye agarres ergonómicos antideslizantes para dominadas pronas, supinas y neutras.",
            material = "Acero al carbono calibre 14 con pintura electrostática",
            dimensions = "100 cm ancho x 55 cm fondo x 25 cm alto",
            weightCapacity = "Soporta hasta 250 kg comprobados",
            sellerName = "IronGrip Calisthenics",
            iconRes = R.drawable.ic_fitness_center
        ),
        Product(
            id = "prod_002",
            name = "Anillas Olímpicas de Madera de Abedul 32mm",
            category = "Anillas",
            price = 145000.0,
            stock = 22,
            description = "Anillas de gimnasia profesional fabricadas en madera maciza de abedul pulido. Agarre superior que absorbe el sudor. Correas de nylon de 4.5 metros con hebillas de alta seguridad.",
            material = "Madera de abedul báltico y cintas de nylon balístico",
            dimensions = "Diámetro externo 23.5 cm, grosor 32 mm, correas 4.5 m",
            weightCapacity = "Carga de rotura 400 kg",
            sellerName = "Apex Movement Store",
            iconRes = R.drawable.ic_fitness_center
        ),
        Product(
            id = "prod_003",
            name = "Paralelas Bajas de Madera (Parallettes)",
            category = "Paralelas",
            price = 120000.0,
            stock = 18,
            description = "Parallettes bajas artesanales ideales para planchas, flexiones profundas, L-sit y handstands en interiores o exteriores. Bases con goma antideslizante.",
            material = "Madera maciza de haya con acabado ecológico cera natural",
            dimensions = "50 cm largo x 14 cm ancho x 10 cm alto",
            weightCapacity = "Soporta hasta 200 kg por par",
            sellerName = "Apex Movement Store",
            iconRes = R.drawable.ic_fitness_center
        ),
        Product(
            id = "prod_004",
            name = "Set de Bandas Elásticas de Resistencia (5 Niveles)",
            category = "Bandas",
            price = 89900.0,
            stock = 40,
            description = "Kit completo de 5 bandas cerradas continuas para progresión de dominadas, movilidad articular y calentamiento dinámico. Incluye bolsa de transporte.",
            material = "Látex 100% malayo de grado médico multicapa",
            dimensions = "Longitud 208 cm cada una, anchos desde 1.3 cm hasta 6.4 cm",
            weightCapacity = "Resistencia progresiva de 5 kg a 75 kg",
            sellerName = "IronGrip Calisthenics",
            iconRes = R.drawable.ic_fitness_center
        ),
        Product(
            id = "prod_005",
            name = "Chaleco con Lastre Ajustable 20kg",
            category = "Lastres",
            price = 230000.0,
            stock = 8,
            description = "Chaleco táctico con 16 lingotes de hierro extraíbles para progresión milimétrica de fuerza en fondos, muscle-ups y dominadas.",
            material = "Cordura 1000D ultrarresistente y lingotes recubiertos",
            dimensions = "Talla única ergonómica con correas de velcro reforzadas",
            weightCapacity = "20 kg de lastre distribuido homogéneamente",
            sellerName = "IronGrip Calisthenics",
            iconRes = R.drawable.ic_fitness_center
        ),
        Product(
            id = "prod_006",
            name = "Cinturón de Lastre en Cuero Genuino",
            category = "Lastres",
            price = 110000.0,
            stock = 14,
            description = "Cinturón lumbar para añadir discos o pesas rusas en dominadas y fondos. Cadena de acero galvanizado con mosquetón reforzado.",
            material = "Cuero de buey de 8 mm y cadena de acero cromado",
            dimensions = "Longitud faja 85 cm, cadena de 90 cm",
            weightCapacity = "Soporta hasta 150 kg en suspensión",
            sellerName = "IronGrip Calisthenics",
            iconRes = R.drawable.ic_fitness_center
        ),
        Product(
            id = "prod_007",
            name = "Paralelas Altas de Acero para Fondos (Dips)",
            category = "Paralelas",
            price = 210000.0,
            stock = 10,
            description = "Estación doble de barras paralelas independientes para dips, leg raises, front lever y planchas. Estabilidad garantizada sin vibración.",
            material = "Acero estructural de 38 mm de diámetro",
            dimensions = "80 cm alto x 65 cm ancho x 40 cm base",
            weightCapacity = "Soporta hasta 220 kg de usuario",
            sellerName = "Apex Movement Store",
            iconRes = R.drawable.ic_fitness_center
        ),
        Product(
            id = "prod_008",
            name = "Magnesio Deportivo en Bloque (Caja x 8 Bloques)",
            category = "Accesorios",
            price = 32000.0,
            stock = 50,
            description = "Carbonato de magnesio puro sin aditivos ni perfumes para garantizar agarre perfecto y evitar resbalones en barras y anillas.",
            material = "100% Carbonato de Magnesio de máxima pureza",
            dimensions = "8 bloques prensados de 56 g cada uno",
            weightCapacity = "448 g netos",
            sellerName = "IronGrip Calisthenics",
            iconRes = R.drawable.ic_fitness_center
        )
    )

    private val cartList = mutableListOf<CartItem>()

    private val ordersList = mutableListOf(
        Order(
            id = "ord_001",
            orderNumber = "ORD-2026-1048",
            date = "02/11/2026 15:30",
            buyerName = "Juan Pablo Acevedo",
            items = listOf(
                CartItem(productsList[0], 1),
                CartItem(productsList[3], 1)
            ),
            total = 274900.0,
            status = OrderStatus.ENTREGADO,
            shippingAddress = "Calle 72 # 11-45, Chapinero, Bogotá",
            paymentMethod = "PSE - Bancolombia",
            radicadoPqr = "PQR-2026-0042"
        ),
        Order(
            id = "ord_002",
            orderNumber = "ORD-2026-1052",
            date = "03/11/2026 18:45",
            buyerName = "Carolina Morales",
            items = listOf(
                CartItem(productsList[1], 1)
            ),
            total = 145000.0,
            status = OrderStatus.ENVIADO,
            shippingAddress = "Av. Circunvalar # 4-80, Cali",
            paymentMethod = "Tarjeta de Crédito Visa",
            radicadoPqr = "PQR-2026-0049"
        ),
        Order(
            id = "ord_003",
            orderNumber = "ORD-2026-1055",
            date = "04/11/2026 09:15",
            buyerName = "David Calistenia Pro",
            items = listOf(
                CartItem(productsList[4], 1),
                CartItem(productsList[5], 1)
            ),
            total = 340000.0,
            status = OrderStatus.PREPARACION,
            shippingAddress = "Carrera 53 # 75-12, Barranquilla",
            paymentMethod = "Nequi",
            radicadoPqr = "PQR-2026-0053"
        )
    )

    // User CRUD
    fun getUsers(): List<User> = usersList.toList()

    fun addUser(user: User) {
        usersList.add(0, user)
    }

    fun updateUser(updated: User) {
        val index = usersList.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            usersList[index] = updated
        }
    }

    fun deleteUser(userId: String) {
        usersList.removeAll { it.id == userId }
    }

    fun toggleUserStatus(userId: String) {
        val user = usersList.find { it.id == userId } ?: return
        user.status = if (user.status == "Activo") "Suspendido" else "Activo"
    }

    // Product CRUD
    fun getProducts(): List<Product> = productsList.toList()

    fun getProductsForVendor(vendorName: String): List<Product> {
        return productsList.filter { it.sellerName.equals(vendorName, ignoreCase = true) }
    }

    fun addProduct(product: Product) {
        productsList.add(0, product)
    }

    fun updateProduct(product: Product) {
        val index = productsList.indexOfFirst { it.id == product.id }
        if (index != -1) {
            productsList[index] = product
        }
    }

    fun deleteProduct(productId: String) {
        productsList.removeAll { it.id == productId }
    }

    fun getProductById(id: String): Product? {
        return productsList.find { it.id == id }
    }

    // Cart Management
    fun getCartItems(): List<CartItem> = cartList.toList()

    fun addToCart(product: Product, quantity: Int = 1) {
        val existing = cartList.find { it.product.id == product.id }
        if (existing != null) {
            existing.quantity += quantity
        } else {
            cartList.add(CartItem(product, quantity))
        }
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        val item = cartList.find { it.product.id == productId } ?: return
        if (quantity <= 0) {
            cartList.remove(item)
        } else {
            item.quantity = quantity
        }
    }

    fun removeFromCart(productId: String) {
        cartList.removeAll { it.product.id == productId }
    }

    fun clearCart() {
        cartList.clear()
    }

    fun getCartCount(): Int = cartList.sumOf { it.quantity }

    fun getCartSubtotal(): Double = cartList.sumOf { it.subtotal }

    // Orders Management
    fun getOrders(): List<Order> = ordersList.toList()

    fun getOrdersForBuyer(buyerName: String): List<Order> {
        return ordersList.filter { it.buyerName.equals(buyerName, ignoreCase = true) }
    }

    fun getOrdersForVendor(vendorName: String): List<Order> {
        return ordersList.filter { order ->
            order.items.any { it.product.sellerName.equals(vendorName, ignoreCase = true) }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        val order = ordersList.find { it.id == orderId } ?: return
        order.status = newStatus
    }

    fun placeOrder(address: String, paymentMethod: String): Order {
        val orderNum = "ORD-2026-" + (1050 + ordersList.size + 1)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val radicado = "PQR-2026-" + String.format(Locale.US, "%04d", (40 + ordersList.size + 1))

        val order = Order(
            id = "ord_" + UUID.randomUUID().toString().take(6),
            orderNumber = orderNum,
            date = currentDate,
            buyerName = currentUser.name,
            items = cartList.map { it.copy() },
            total = getCartSubtotal() + 15000.0, // Subtotal + envío
            status = OrderStatus.PENDIENTE,
            shippingAddress = address,
            paymentMethod = paymentMethod,
            radicadoPqr = radicado
        )
        ordersList.add(0, order)

        // Descontar inventario
        cartList.forEach { cartItem ->
            val p = productsList.find { it.id == cartItem.product.id }
            if (p != null) {
                p.stock = (p.stock - cartItem.quantity).coerceAtLeast(0)
            }
        }
        clearCart()
        return order
    }

    // Role helper
    fun setRole(role: UserRole) {
        currentRole = role
        currentUser = when (role) {
            UserRole.ADMIN -> usersList[0]
            UserRole.VENDOR -> usersList[1]
            UserRole.BUYER -> usersList[2]
        }
    }

    // Format currency COP
    fun formatCOP(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        format.maximumFractionDigits = 0
        return format.format(amount) + " COP"
    }
}
