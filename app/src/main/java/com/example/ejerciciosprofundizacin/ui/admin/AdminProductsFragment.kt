package com.example.ejerciciosprofundizacin.ui.admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ejerciciosprofundizacin.R
import com.example.ejerciciosprofundizacin.data.AppDataRepository
import com.example.ejerciciosprofundizacin.databinding.FragmentAdminProductsBinding
import com.example.ejerciciosprofundizacin.model.Product
import com.example.ejerciciosprofundizacin.ui.adapter.AdminProductsAdapter
import java.util.UUID

class AdminProductsFragment : Fragment() {

    private var _binding: FragmentAdminProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdminProductsAdapter
    private var searchQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()

        binding.fabAdminAddProduct.setOnClickListener {
            showAddProductDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = AdminProductsAdapter(
            products = getFilteredProducts(),
            onEditClick = { product -> showEditProductDialog(product) },
            onDeleteClick = { product -> showDeleteProductDialog(product) }
        )
        binding.rvAdminProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAdminProducts.adapter = adapter
    }

    private fun setupSearch() {
        binding.etProductSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString()?.trim() ?: ""
                refreshList()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun getFilteredProducts(): List<Product> {
        return AppDataRepository.getProducts().filter { product ->
            searchQuery.isEmpty() ||
                    product.name.contains(searchQuery, ignoreCase = true) ||
                    product.category.contains(searchQuery, ignoreCase = true) ||
                    product.sellerName.contains(searchQuery, ignoreCase = true)
        }
    }

    private fun refreshList() {
        adapter.updateData(getFilteredProducts())
    }

    private fun showAddProductDialog() {
        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 16)
        }

        val etName = EditText(context).apply { hint = "Nombre del producto" }
        val etCategory = EditText(context).apply { hint = "Categoría (Barras, Anillas, etc.)" }
        val etPrice = EditText(context).apply { hint = "Precio en COP (ej. 150000)" }
        val etStock = EditText(context).apply { hint = "Stock / Unidades (ej. 20)" }
        val etMaterial = EditText(context).apply { hint = "Material (ej. Acero al carbono)" }
        val etSeller = EditText(context).apply { hint = "Nombre del vendedor / Tienda" }

        layout.addView(etName)
        layout.addView(etCategory)
        layout.addView(etPrice)
        layout.addView(etStock)
        layout.addView(etMaterial)
        layout.addView(etSeller)

        AlertDialog.Builder(context)
            .setTitle("Nuevo Producto Deportivo")
            .setView(layout)
            .setPositiveButton("Publicar") { _, _ ->
                val name = etName.text.toString().trim()
                val price = etPrice.text.toString().toDoubleOrNull() ?: 100000.0
                val stock = etStock.text.toString().toIntOrNull() ?: 10
                if (name.isNotEmpty()) {
                    val newProd = Product(
                        id = "prod_" + UUID.randomUUID().toString().take(6),
                        name = name,
                        category = etCategory.text.toString().trim().ifEmpty { "Calistenia" },
                        price = price,
                        stock = stock,
                        description = "Accesorio oficial para entrenamiento de fuerza y calistenia.",
                        material = etMaterial.text.toString().trim().ifEmpty { "Acero reforzado" },
                        dimensions = "Estándar oficial",
                        weightCapacity = "Soporta hasta 200 kg",
                        sellerName = etSeller.text.toString().trim().ifEmpty { "FitMarket Oficial" },
                        iconRes = R.drawable.ic_fitness_center
                    )
                    AppDataRepository.addProduct(newProd)
                    refreshList()
                    Toast.makeText(context, "Producto publicado con éxito", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEditProductDialog(product: Product) {
        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 16)
        }

        val etName = EditText(context).apply { setText(product.name); hint = "Nombre" }
        val etPrice = EditText(context).apply { setText(product.price.toLong().toString()); hint = "Precio" }
        val etStock = EditText(context).apply { setText(product.stock.toString()); hint = "Stock" }
        val etMaterial = EditText(context).apply { setText(product.material); hint = "Material" }

        layout.addView(etName)
        layout.addView(etPrice)
        layout.addView(etStock)
        layout.addView(etMaterial)

        AlertDialog.Builder(context)
            .setTitle("Editar: ${product.name}")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                product.name = etName.text.toString().trim()
                product.price = etPrice.text.toString().toDoubleOrNull() ?: product.price
                product.stock = etStock.text.toString().toIntOrNull() ?: product.stock
                product.material = etMaterial.text.toString().trim()
                AppDataRepository.updateProduct(product)
                refreshList()
                Toast.makeText(context, "Producto modificado correctamente", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDeleteProductDialog(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Producto")
            .setMessage("¿Deseas retirar '${product.name}' de la plataforma?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Eliminar") { _, _ ->
                AppDataRepository.deleteProduct(product.id)
                refreshList()
                Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
