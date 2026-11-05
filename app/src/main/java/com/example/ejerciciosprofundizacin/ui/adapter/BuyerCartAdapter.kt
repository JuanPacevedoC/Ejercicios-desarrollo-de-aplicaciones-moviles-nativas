package com.example.ejerciciosprofundizacin.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ejerciciosprofundizacin.data.AppDataRepository
import com.example.ejerciciosprofundizacin.databinding.ItemCartProductBinding
import com.example.ejerciciosprofundizacin.model.CartItem

class BuyerCartAdapter(
    private var items: List<CartItem>,
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val onRemoveClick: (CartItem) -> Unit
) : RecyclerView.Adapter<BuyerCartAdapter.CartViewHolder>() {

    fun updateData(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class CartViewHolder(private val binding: ItemCartProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItem) {
            binding.tvCartProductName.text = item.product.name
            binding.tvCartProductUnitPrice.text = "Unidad: ${AppDataRepository.formatCOP(item.product.price)}"
            binding.tvCartQuantity.text = item.quantity.toString()
            binding.tvCartItemSubtotal.text = AppDataRepository.formatCOP(item.subtotal)

            binding.btnCartMinus.setOnClickListener {
                onQuantityChanged(item, item.quantity - 1)
            }
            binding.btnCartPlus.setOnClickListener {
                onQuantityChanged(item, item.quantity + 1)
            }
            binding.btnCartRemove.setOnClickListener {
                onRemoveClick(item)
            }
        }
    }
}
