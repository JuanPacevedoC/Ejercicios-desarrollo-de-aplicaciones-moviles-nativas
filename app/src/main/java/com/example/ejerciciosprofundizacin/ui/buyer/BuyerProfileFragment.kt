package com.example.ejerciciosprofundizacin.ui.buyer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.ejerciciosprofundizacin.R
import com.example.ejerciciosprofundizacin.data.AppDataRepository
import com.example.ejerciciosprofundizacin.databinding.FragmentBuyerProfileBinding

class BuyerProfileFragment : Fragment() {

    private var _binding: FragmentBuyerProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuyerProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateProfile()

        binding.btnEditProfileData.setOnClickListener {
            showEditProfileDialog()
        }

        binding.btnViewMyOrders.setOnClickListener {
            showMyOrdersDialog()
        }

        binding.btnPqrSupport.setOnClickListener {
            showPqrDialog()
        }
    }

    private fun populateProfile() {
        val user = AppDataRepository.currentUser
        binding.tvProfileName.text = user.name
        binding.tvProfileEmail.text = user.email
        binding.tvProfilePhone.text = user.phone
        binding.tvProfileAddress.text = user.address
    }

    private fun showEditProfileDialog() {
        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 16)
        }

        val etPhone = EditText(context).apply {
            setText(AppDataRepository.currentUser.phone)
            hint = "Teléfono de contacto"
        }
        val etAddress = EditText(context).apply {
            setText(AppDataRepository.currentUser.address)
            hint = "Dirección de entrega"
        }

        layout.addView(etPhone)
        layout.addView(etAddress)

        AlertDialog.Builder(context)
            .setTitle("Actualizar Mis Datos")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                AppDataRepository.currentUser.phone = etPhone.text.toString().trim()
                AppDataRepository.currentUser.address = etAddress.text.toString().trim()
                populateProfile()
                Toast.makeText(context, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showMyOrdersDialog() {
        val orders = AppDataRepository.getOrders()
        val itemsList = if (orders.isEmpty()) {
            "Aún no has realizado compras en la tienda."
        } else {
            orders.joinToString("\n\n") { order ->
                "• ${order.orderNumber} (${order.date})\n" +
                        "  Estado: ${order.status.label}\n" +
                        "  Total: ${AppDataRepository.formatCOP(order.total)}\n" +
                        "  Artículos: ${order.items.joinToString(", ") { it.product.name + " (x" + it.quantity + ")" }}"
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Mis Pedidos Realizados")
            .setMessage(itemsList)
            .setIcon(R.drawable.ic_receipt_long)
            .setPositiveButton("Cerrar", null)
            .show()
    }

    private fun showPqrDialog() {
        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 16)
        }

        val etPqr = EditText(context).apply {
            hint = "Escribe tu petición, queja o solicitud de retracto..."
            minLines = 3
        }
        layout.addView(etPqr)

        AlertDialog.Builder(context)
            .setTitle("Canal de Peticiones, Quejas y Reclamos (PQR)")
            .setMessage("Conforme a la Circular 006 de 2014 de la SIC y el Estatuto del Consumidor (Ley 1480 de 2011), tu solicitud recibirá un número de radicado oficial con fecha y hora.")
            .setView(layout)
            .setPositiveButton("Radicar") { _, _ ->
                val radicado = "PQR-2026-" + (1000..9999).random()
                AlertDialog.Builder(context)
                    .setTitle("PQR Radicada Exitosamente")
                    .setMessage("Tu radicado oficial es: $radicado\nTiempo de respuesta legal: máximo 15 días hábiles.\nHemos enviado constancia a tu correo electrónico.")
                    .setPositiveButton("Entendido", null)
                    .show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
