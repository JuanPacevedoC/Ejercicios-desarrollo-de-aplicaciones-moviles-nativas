package com.example.ejerciciosprofundizacin.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ejerciciosprofundizacin.R
import com.example.ejerciciosprofundizacin.data.AppDataRepository
import com.example.ejerciciosprofundizacin.databinding.ItemVendorOrderBinding
import com.example.ejerciciosprofundizacin.model.Order
import com.example.ejerciciosprofundizacin.model.OrderStatus

class VendorOrdersAdapter(
    private var orders: List<Order>,
    private val onAdvanceStatusClick: (Order) -> Unit
) : RecyclerView.Adapter<VendorOrdersAdapter.OrderViewHolder>() {

    fun updateData(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemVendorOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(private val binding: ItemVendorOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.tvOrderNumber.text = order.orderNumber
            binding.tvOrderDateAndCustomer.text = "${order.date} • Comprador: ${order.buyerName}"
            binding.tvOrderAddress.text = "Envío a: ${order.shippingAddress}"

            val itemsText = order.items.joinToString("\n") { "• ${it.product.name} (x${it.quantity})" }
            binding.tvOrderItemsSummary.text = itemsText
            binding.tvOrderTotal.text = AppDataRepository.formatCOP(order.total)
            binding.tvOrderStatusBadge.text = order.status.label

            val context = binding.root.context
            when (order.status) {
                OrderStatus.PENDIENTE -> {
                    binding.tvOrderStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.statusPending))
                    binding.btnAdvanceStatus.text = "Marcar en Preparación"
                    binding.btnAdvanceStatus.isEnabled = true
                }
                OrderStatus.PREPARACION -> {
                    binding.tvOrderStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.statusProcessing))
                    binding.btnAdvanceStatus.text = "Marcar como Enviado"
                    binding.btnAdvanceStatus.isEnabled = true
                }
                OrderStatus.ENVIADO -> {
                    binding.tvOrderStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.statusShipped))
                    binding.btnAdvanceStatus.text = "Marcar como Entregado"
                    binding.btnAdvanceStatus.isEnabled = true
                }
                OrderStatus.ENTREGADO -> {
                    binding.tvOrderStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.statusDelivered))
                    binding.btnAdvanceStatus.text = "Pedido Completado ✓"
                    binding.btnAdvanceStatus.isEnabled = false
                }
            }

            binding.btnAdvanceStatus.setOnClickListener { onAdvanceStatusClick(order) }
        }
    }
}
