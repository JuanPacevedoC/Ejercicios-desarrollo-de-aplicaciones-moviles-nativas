package com.example.ejerciciosprofundizacin.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ejerciciosprofundizacin.data.AppDataRepository
import com.example.ejerciciosprofundizacin.databinding.ItemVendorProductBinding
import com.example.ejerciciosprofundizacin.model.Product

class VendorProductsAdapter(
    private var products: List<Product>,
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<VendorProductsAdapter.VendorProductViewHolder>() {

    fun updateData(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorProductViewHolder {
        val binding = ItemVendorProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VendorProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VendorProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    inner class VendorProductViewHolder(private val binding: ItemVendorProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvVendorProductName.text = product.name
            binding.tvVendorProductCategory.text = "Categoría: ${product.category}"
            binding.tvVendorProductPrice.text = AppDataRepository.formatCOP(product.price)
            binding.tvVendorProductStock.text = "Disponibles: ${product.stock} unidades"
            binding.tvVendorProductWeight.text = product.weightCapacity

            binding.btnVendorEditProduct.setOnClickListener { onEditClick(product) }
            binding.btnVendorDeleteProduct.setOnClickListener { onDeleteClick(product) }
        }
    }
}
