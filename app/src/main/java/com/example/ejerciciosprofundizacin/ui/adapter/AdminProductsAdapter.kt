package com.example.ejerciciosprofundizacin.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ejerciciosprofundizacin.data.AppDataRepository
import com.example.ejerciciosprofundizacin.databinding.ItemAdminProductBinding
import com.example.ejerciciosprofundizacin.model.Product

class AdminProductsAdapter(
    private var products: List<Product>,
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<AdminProductsAdapter.ProductViewHolder>() {

    fun updateData(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemAdminProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    inner class ProductViewHolder(private val binding: ItemAdminProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvAdminProductName.text = product.name
            binding.tvAdminProductSeller.text = "Vendedor: ${product.sellerName}"
            binding.tvAdminProductCategory.text = product.category
            binding.tvAdminProductPrice.text = AppDataRepository.formatCOP(product.price)
            binding.tvAdminProductStock.text = "Stock: ${product.stock} unidades"

            binding.btnAdminEditProduct.setOnClickListener { onEditClick(product) }
            binding.btnAdminDeleteProduct.setOnClickListener { onDeleteClick(product) }
        }
    }
}
