package com.example.ejerciciosprofundizacin.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ejerciciosprofundizacin.data.AppDataRepository
import com.example.ejerciciosprofundizacin.databinding.ItemProductCardBinding
import com.example.ejerciciosprofundizacin.model.Product

class BuyerProductAdapter(
    private var products: List<Product>,
    private val onProductClick: (Product) -> Unit,
    private val onAddToCartClick: (Product) -> Unit
) : RecyclerView.Adapter<BuyerProductAdapter.ProductViewHolder>() {

    fun updateData(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    inner class ProductViewHolder(private val binding: ItemProductCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvProductCategoryBadge.text = product.category.uppercase()
            binding.tvProductName.text = product.name
            binding.tvProductSeller.text = "Vendido por ${product.sellerName}"
            binding.tvProductSpecsPreview.text = "${product.material} • ${product.weightCapacity}"
            binding.tvProductPrice.text = AppDataRepository.formatCOP(product.price)
            binding.tvProductStock.text = "${product.stock} unidades disponibles"

            binding.root.setOnClickListener { onProductClick(product) }
            binding.btnAddToCart.setOnClickListener { onAddToCartClick(product) }
        }
    }
}
