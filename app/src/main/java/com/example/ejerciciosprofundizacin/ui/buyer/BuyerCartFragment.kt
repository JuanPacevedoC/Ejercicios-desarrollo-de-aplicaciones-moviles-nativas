package com.example.ejerciciosprofundizacin.ui.buyer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ejerciciosprofundizacin.MainActivity
import com.example.ejerciciosprofundizacin.data.AppDataRepository
import com.example.ejerciciosprofundizacin.databinding.FragmentBuyerCartBinding
import com.example.ejerciciosprofundizacin.ui.adapter.BuyerCartAdapter

class BuyerCartFragment : Fragment() {

    private var _binding: FragmentBuyerCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BuyerCartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuyerCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        updateCartState()

        binding.btnEmptyExplore.setOnClickListener {
            (activity as? MainActivity)?.loadFragment(BuyerCatalogFragment(), "Catálogo FitMarket")
        }

        binding.btnProceedCheckout.setOnClickListener {
            if (AppDataRepository.getCartItems().isEmpty()) {
                Toast.makeText(requireContext(), "Agrega productos antes de proceder al pago", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            (activity as? MainActivity)?.loadFragment(BuyerCheckoutFragment(), "Pasarela de Pagos")
        }
    }

    private fun setupRecyclerView() {
        adapter = BuyerCartAdapter(
            items = AppDataRepository.getCartItems(),
            onQuantityChanged = { item, newQty ->
                AppDataRepository.updateCartQuantity(item.product.id, newQty)
                updateCartState()
                (activity as? MainActivity)?.updateCartBadge()
            },
            onRemoveClick = { item ->
                AppDataRepository.removeFromCart(item.product.id)
                updateCartState()
                (activity as? MainActivity)?.updateCartBadge()
                Toast.makeText(requireContext(), "${item.product.name} eliminado del carrito", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvCartItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCartItems.adapter = adapter
    }

    private fun updateCartState() {
        val items = AppDataRepository.getCartItems()
        adapter.updateData(items)

        if (items.isEmpty()) {
            binding.layoutCartEmpty.visibility = View.VISIBLE
            binding.rvCartItems.visibility = View.GONE
            binding.cardCartSummary.visibility = View.GONE
        } else {
            binding.layoutCartEmpty.visibility = View.GONE
            binding.rvCartItems.visibility = View.VISIBLE
            binding.cardCartSummary.visibility = View.VISIBLE

            val subtotal = AppDataRepository.getCartSubtotal()
            val shipping = 15000.0
            val total = subtotal + shipping

            binding.tvCartSummarySubtotal.text = AppDataRepository.formatCOP(subtotal)
            binding.tvCartSummaryShipping.text = AppDataRepository.formatCOP(shipping)
            binding.tvCartSummaryTotal.text = AppDataRepository.formatCOP(total)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
