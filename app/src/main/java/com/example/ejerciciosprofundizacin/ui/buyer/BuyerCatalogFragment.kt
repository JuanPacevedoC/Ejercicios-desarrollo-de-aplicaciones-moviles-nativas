package com.example.ejerciciosprofundizacin.ui.buyer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ejerciciosprofundizacin.MainActivity
import com.example.ejerciciosprofundizacin.R
import com.example.ejerciciosprofundizacin.data.AppDataRepository
import com.example.ejerciciosprofundizacin.databinding.FragmentBuyerCatalogBinding
import com.example.ejerciciosprofundizacin.model.Product
import com.example.ejerciciosprofundizacin.ui.adapter.BuyerProductAdapter

class BuyerCatalogFragment : Fragment() {

    private var _binding: FragmentBuyerCatalogBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BuyerProductAdapter

    private var selectedCategory: String? = null
    private var searchQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuyerCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchAndFilters()
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
        binding.rvCatalogProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCatalogProducts.adapter = adapter
    }

    private fun setupSearchAndFilters() {
        binding.etCatalogSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString()?.trim() ?: ""
                refreshList()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.chipGroupCatalogFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedCategory = when {
                checkedIds.contains(R.id.chipCatBars) -> "Barras"
                checkedIds.contains(R.id.chipCatRings) -> "Anillas"
                checkedIds.contains(R.id.chipCatParallettes) -> "Paralelas"
                checkedIds.contains(R.id.chipCatBands) -> "Bandas"
                checkedIds.contains(R.id.chipCatWeights) -> "Lastres"
                else -> null
            }
            refreshList()
        }
    }

    private fun getFilteredProducts(): List<Product> {
        return AppDataRepository.getProducts().filter { product ->
            val matchesCategory = selectedCategory == null || product.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isEmpty() ||
                    product.name.contains(searchQuery, ignoreCase = true) ||
                    product.category.contains(searchQuery, ignoreCase = true) ||
                    product.material.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    private fun refreshList() {
        adapter.updateData(getFilteredProducts())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
