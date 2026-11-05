package com.example.ejerciciosprofundizacin.ui.buyer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.ejerciciosprofundizacin.MainActivity
import com.example.ejerciciosprofundizacin.R
import com.example.ejerciciosprofundizacin.data.AppDataRepository
import com.example.ejerciciosprofundizacin.databinding.FragmentBuyerCheckoutBinding

class BuyerCheckoutFragment : Fragment() {

    private var _binding: FragmentBuyerCheckoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuyerCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = AppDataRepository.getCartItems()
        val total = AppDataRepository.getCartSubtotal() + 15000.0

        val summaryText = if (items.isNotEmpty()) {
            items.joinToString(", ") { "${it.product.name} (x${it.quantity})" }
        } else {
            "Sin artículos en carrito"
        }
        binding.tvCheckoutItemsSummary.text = summaryText
        binding.tvCheckoutTotal.text = AppDataRepository.formatCOP(total)
        binding.etCheckoutAddress.setText(AppDataRepository.currentUser.address)

        binding.btnBackCheckout.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnConfirmPayment.setOnClickListener {
            val address = binding.etCheckoutAddress.text.toString().trim()
            if (address.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor ingresa la dirección de entrega", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val paymentMethod = when (binding.rgPaymentMethods.checkedRadioButtonId) {
                R.id.rbCreditCard -> "Tarjeta de Crédito / Débito"
                R.id.rbNequi -> "Nequi / Daviplata"
                else -> "PSE - Débito Bancario"
            }

            val order = AppDataRepository.placeOrder(address, paymentMethod)
            (activity as? MainActivity)?.updateCartBadge()

            AlertDialog.Builder(requireContext())
                .setTitle("¡Pago y Orden Confirmada!")
                .setMessage(
                    "Tu orden ${order.orderNumber} ha sido generada exitosamente.\n\n" +
                            "• Total debitado: ${AppDataRepository.formatCOP(order.total)}\n" +
                            "• Método: $paymentMethod\n" +
                            "• Dirección: $address\n" +
                            "• Radicado PQR SIC: ${order.radicadoPqr}\n" +
                            "• Comprobante digital: Firmado y válido bajo Ley 527 de 1999\n\n" +
                            "El vendedor despachará tu equipamiento de calistenia en las próximas 24 horas hábiles."
                )
                .setIcon(R.drawable.ic_check_circle)
                .setPositiveButton("Ir al Inicio") { _, _ ->
                    (activity as? MainActivity)?.loadFragment(BuyerHomeFragment(), "FitMarket Calisthenics")
                }
                .setCancelable(false)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
