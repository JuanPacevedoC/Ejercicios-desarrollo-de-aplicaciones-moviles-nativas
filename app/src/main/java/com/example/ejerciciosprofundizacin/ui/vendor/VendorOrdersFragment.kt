package com.example.ejerciciosprofundizacin.ui.vendor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ejerciciosprofundizacin.data.AppDataRepository
import com.example.ejerciciosprofundizacin.databinding.FragmentVendorOrdersBinding
import com.example.ejerciciosprofundizacin.model.OrderStatus
import com.example.ejerciciosprofundizacin.ui.adapter.VendorOrdersAdapter

class VendorOrdersFragment : Fragment() {

    private var _binding: FragmentVendorOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: VendorOrdersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVendorOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = VendorOrdersAdapter(
            orders = AppDataRepository.getOrders(),
            onAdvanceStatusClick = { order ->
                val nextStatus = when (order.status) {
                    OrderStatus.PENDIENTE -> OrderStatus.PREPARACION
                    OrderStatus.PREPARACION -> OrderStatus.ENVIADO
                    OrderStatus.ENVIADO -> OrderStatus.ENTREGADO
                    OrderStatus.ENTREGADO -> OrderStatus.ENTREGADO
                }
                AppDataRepository.updateOrderStatus(order.id, nextStatus)
                adapter.updateData(AppDataRepository.getOrders())
                Toast.makeText(requireContext(), "Orden #${order.orderNumber} actualizada a: ${nextStatus.label}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvVendorOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVendorOrders.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
