package com.example.ejerciciosprofundizacin.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ejerciciosprofundizacin.MainActivity
import com.example.ejerciciosprofundizacin.data.AppDataRepository
import com.example.ejerciciosprofundizacin.databinding.FragmentAdminDashboardBinding

class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Actualizar métricas dinámicas
        val totalRevenue = AppDataRepository.getOrders().sumOf { it.total }
        binding.tvKpiSales.text = AppDataRepository.formatCOP(totalRevenue)
        binding.tvKpiOrders.text = "${AppDataRepository.getOrders().size} órdenes"
        binding.tvKpiUsers.text = "${AppDataRepository.getUsers().size} registrados"
        binding.tvKpiProducts.text = "${AppDataRepository.getProducts().size} productos"

        // Accesos rápidos
        binding.btnGoUsers.setOnClickListener {
            (activity as? MainActivity)?.loadFragment(AdminUsersFragment(), "Gestión de Usuarios")
        }
        binding.btnGoProducts.setOnClickListener {
            (activity as? MainActivity)?.loadFragment(AdminProductsFragment(), "Gestión de Productos")
        }
        binding.btnGoReports.setOnClickListener {
            (activity as? MainActivity)?.loadFragment(AdminReportsFragment(), "Reporte de Ventas")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
