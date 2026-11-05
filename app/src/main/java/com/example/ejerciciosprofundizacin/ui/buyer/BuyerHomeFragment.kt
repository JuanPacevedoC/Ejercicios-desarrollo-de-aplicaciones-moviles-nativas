package com.example.ejerciciosprofundizacin.ui.buyer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ejerciciosprofundizacin.MainActivity
import com.example.ejerciciosprofundizacin.R
import com.example.ejerciciosprofundizacin.data.AppDataRepository
import com.example.ejerciciosprofundizacin.databinding.FragmentBuyerHomeBinding
import com.example.ejerciciosprofundizacin.model.Product
import com.example.ejerciciosprofundizacin.ui.adapter.BuyerProductAdapter

class BuyerHomeFragment : Fragment() {

    private var _binding: FragmentBuyerHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BuyerProductAdapter
    private var selectedCategory: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuyerHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupCategoryChips()

        binding.btnBannerExplore.setOnClickListener {
            (activity as? MainActivity)?.loadFragment(BuyerCatalogFragment(), "Catálogo FitMarket")
        }
    }

    private fun setupRecyclerView() {
        adapter = BuyerProductAdapter(
            products = getFilteredProducts(),
            onProductClick = { product ->
                (activity as? MainActivity)?.openProductDetail(product.id)
            },
            onAddToCartClick = { product ->
                AppDataRepository.addToCart(product, 1)
                (activity as? MainActivity)?.updateCartBadge()
                Toast.makeText(requireContext(), "¡${product.name} añadido al carrito!", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvHomeProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHomeProducts.adapter = adapter
    }

    private fun setupCategoryChips() {
        binding.chipGroupHomeCategories.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedCategory = when {
                checkedIds.contains(R.id.chipCatBars) -> "Barras"
                checkedIds.contains(R.id.chipCatRings) -> "Anillas"
                checkedIds.contains(R.id.chipCatParallettes) -> "Paralelas"
                checkedIds.contains(R.id.chipCatBands) -> "Bandas"
                checkedIds.contains(R.id.chipCatWeights) -> "Lastres"
                else -> null
            }
            adapter.updateData(getFilteredProducts())
        }
    }

    private fun getFilteredProducts(): List<Product> {
        return if (selectedCategory == null) {
            AppDataRepository.getProducts()
        } else {
            AppDataRepository.getProducts().filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
