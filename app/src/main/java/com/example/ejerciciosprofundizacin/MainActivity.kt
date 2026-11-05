package com.example.ejerciciosprofundizacin

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.ejerciciosprofundizacin.data.AppDataRepository
import com.example.ejerciciosprofundizacin.databinding.ActivityMainBinding
import com.example.ejerciciosprofundizacin.model.UserRole
import com.example.ejerciciosprofundizacin.ui.admin.AdminDashboardFragment
import com.example.ejerciciosprofundizacin.ui.admin.AdminProductsFragment
import com.example.ejerciciosprofundizacin.ui.admin.AdminReportsFragment
import com.example.ejerciciosprofundizacin.ui.admin.AdminUsersFragment
import com.example.ejerciciosprofundizacin.ui.auth.LoginActivity
import com.example.ejerciciosprofundizacin.ui.buyer.BuyerCartFragment
import com.example.ejerciciosprofundizacin.ui.buyer.BuyerCatalogFragment
import com.example.ejerciciosprofundizacin.ui.buyer.BuyerHomeFragment
import com.example.ejerciciosprofundizacin.ui.buyer.BuyerProductDetailFragment
import com.example.ejerciciosprofundizacin.ui.buyer.BuyerProfileFragment
import com.example.ejerciciosprofundizacin.ui.vendor.VendorOrdersFragment
import com.example.ejerciciosprofundizacin.ui.vendor.VendorProductsFragment
import com.example.ejerciciosprofundizacin.ui.vendor.VendorProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDrawer()
        applyRoleConfiguration()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    finish()
                }
            }
        })
    }

    private fun setupToolbar() {
        binding.topToolbar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.chipToolbarRole.setOnClickListener {
            showRoleSwitcherDialog()
        }
    }

    private fun setupDrawer() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            when (menuItem.itemId) {
                // Buyer Drawer
                R.id.drawer_buyer_home -> loadFragment(BuyerHomeFragment(), "FitMarket Calisthenics")
                R.id.drawer_buyer_catalog -> loadFragment(BuyerCatalogFragment(), "Catálogo Deportivo")
                R.id.drawer_buyer_cart -> loadFragment(BuyerCartFragment(), "Mi Carrito")
                R.id.drawer_buyer_orders -> {
                    loadFragment(BuyerProfileFragment(), "Mis Compras")
                }
                R.id.drawer_buyer_profile -> loadFragment(BuyerProfileFragment(), "Mi Perfil")

                // Vendor Drawer
                R.id.drawer_vendor_products -> loadFragment(VendorProductsFragment(), "Panel de Productos")
                R.id.drawer_vendor_orders -> loadFragment(VendorOrdersFragment(), "Historial de Pedidos")
                R.id.drawer_vendor_profile -> loadFragment(VendorProfileFragment(), "Perfil de Vendedor")

                // Admin Drawer
                R.id.drawer_admin_dashboard -> loadFragment(AdminDashboardFragment(), "Dashboard Principal")
                R.id.drawer_admin_users -> loadFragment(AdminUsersFragment(), "Gestión de Usuarios")
                R.id.drawer_admin_products -> loadFragment(AdminProductsFragment(), "Gestión de Productos")
                R.id.drawer_admin_reports -> loadFragment(AdminReportsFragment(), "Reporte de Ventas")

                // Common
                R.id.drawer_switch_role -> showRoleSwitcherDialog()
                R.id.drawer_logout -> performLogout()
            }
            true
        }
    }

    fun applyRoleConfiguration() {
        val currentRole = AppDataRepository.currentRole
        val user = AppDataRepository.currentUser

        // Actualizar chip en toolbar
        binding.chipToolbarRole.text = currentRole.displayName
        val (roleColor, chipBg) = when (currentRole) {
            UserRole.ADMIN -> Pair(R.color.roleAdmin, R.color.roleAdminLight)
            UserRole.VENDOR -> Pair(R.color.roleVendor, R.color.roleVendorLight)
            UserRole.BUYER -> Pair(R.color.roleBuyer, R.color.roleBuyerLight)
        }
        binding.chipToolbarRole.setTextColor(ContextCompat.getColor(this, roleColor))
        binding.chipToolbarRole.setChipIconTintResource(roleColor)
        binding.chipToolbarRole.setChipBackgroundColorResource(chipBg)

        // Actualizar header del drawer
        val headerView = binding.navigationView.getHeaderView(0)
        val tvHeaderName = headerView.findViewById<TextView>(R.id.tvHeaderName)
        val tvHeaderEmail = headerView.findViewById<TextView>(R.id.tvHeaderEmail)
        val tvHeaderRole = headerView.findViewById<TextView>(R.id.tvHeaderRole)
        val ivHeaderAvatar = headerView.findViewById<ImageView>(R.id.ivHeaderAvatar)

        tvHeaderName.text = user.name
        tvHeaderEmail.text = user.email
        tvHeaderRole.text = "ROL ACTIVO: ${currentRole.displayName.uppercase()}"
        tvHeaderRole.setTextColor(ContextCompat.getColor(this, roleColor))
        ivHeaderAvatar.setColorFilter(ContextCompat.getColor(this, roleColor))

        // Configurar menú de Navigation Drawer según el rol
        binding.navigationView.menu.clear()
        when (currentRole) {
            UserRole.ADMIN -> binding.navigationView.inflateMenu(R.menu.drawer_menu_admin)
            UserRole.VENDOR -> binding.navigationView.inflateMenu(R.menu.drawer_menu_vendor)
            UserRole.BUYER -> binding.navigationView.inflateMenu(R.menu.drawer_menu_buyer)
        }

        // Configurar menú de BottomNavigation según el rol
        binding.bottomNavigation.menu.clear()
        when (currentRole) {
            UserRole.ADMIN -> {
                binding.bottomNavigation.inflateMenu(R.menu.bottom_nav_admin)
                setupBottomNavAdmin()
                loadFragment(AdminDashboardFragment(), "Dashboard Principal", addToBackStack = false)
            }
            UserRole.VENDOR -> {
                binding.bottomNavigation.inflateMenu(R.menu.bottom_nav_vendor)
                setupBottomNavVendor()
                loadFragment(VendorProductsFragment(), "Panel de Productos", addToBackStack = false)
            }
            UserRole.BUYER -> {
                binding.bottomNavigation.inflateMenu(R.menu.bottom_nav_buyer)
                setupBottomNavBuyer()
                loadFragment(BuyerHomeFragment(), "FitMarket Calisthenics", addToBackStack = false)
                updateCartBadge()
            }
        }
    }

    private fun setupBottomNavBuyer() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_buyer_home -> {
                    loadFragment(BuyerHomeFragment(), "FitMarket Calisthenics", addToBackStack = false)
                    true
                }
                R.id.menu_buyer_catalog -> {
                    loadFragment(BuyerCatalogFragment(), "Catálogo FitMarket", addToBackStack = false)
                    true
                }
                R.id.menu_buyer_cart -> {
                    loadFragment(BuyerCartFragment(), "Mi Carrito", addToBackStack = false)
                    true
                }
                R.id.menu_buyer_profile -> {
                    loadFragment(BuyerProfileFragment(), "Mi Perfil", addToBackStack = false)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupBottomNavVendor() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_vendor_products -> {
                    loadFragment(VendorProductsFragment(), "Panel de Productos", addToBackStack = false)
                    true
                }
                R.id.menu_vendor_orders -> {
                    loadFragment(VendorOrdersFragment(), "Historial de Pedidos", addToBackStack = false)
                    true
                }
                R.id.menu_vendor_profile -> {
                    loadFragment(VendorProfileFragment(), "Perfil de Vendedor", addToBackStack = false)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupBottomNavAdmin() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_admin_dashboard -> {
                    loadFragment(AdminDashboardFragment(), "Dashboard Principal", addToBackStack = false)
                    true
                }
                R.id.menu_admin_users -> {
                    loadFragment(AdminUsersFragment(), "Gestión de Usuarios", addToBackStack = false)
                    true
                }
                R.id.menu_admin_products -> {
                    loadFragment(AdminProductsFragment(), "Gestión de Productos", addToBackStack = false)
                    true
                }
                R.id.menu_admin_reports -> {
                    loadFragment(AdminReportsFragment(), "Reporte de Ventas", addToBackStack = false)
                    true
                }
                else -> false
            }
        }
    }

    fun loadFragment(fragment: Fragment, title: String, addToBackStack: Boolean = true) {
        binding.topToolbar.title = title
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    fun openProductDetail(productId: String) {
        val detailFragment = BuyerProductDetailFragment.newInstance(productId)
        loadFragment(detailFragment, "Detalle del Producto")
    }

    fun updateCartBadge() {
        if (AppDataRepository.currentRole != UserRole.BUYER) return
        val count = AppDataRepository.getCartCount()
        val badge = binding.bottomNavigation.getOrCreateBadge(R.id.menu_buyer_cart)
        if (count > 0) {
            badge.isVisible = true
            badge.number = count
        } else {
            badge.isVisible = false
        }
    }

    private fun showRoleSwitcherDialog() {
        val roles = arrayOf("Comprador", "Vendedor", "Administrador")
        val currentIndex = when (AppDataRepository.currentRole) {
            UserRole.BUYER -> 0
            UserRole.VENDOR -> 1
            UserRole.ADMIN -> 2
        }

        AlertDialog.Builder(this)
            .setTitle("Cambiar Rol de Demostración")
            .setSingleChoiceItems(roles, currentIndex) { dialog, which ->
                val newRole = when (which) {
                    0 -> UserRole.BUYER
                    1 -> UserRole.VENDOR
                    else -> UserRole.ADMIN
                }
                AppDataRepository.setRole(newRole)
                applyRoleConfiguration()
                Toast.makeText(this, "Perfil cambiado a: ${newRole.displayName}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun performLogout() {
        Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
