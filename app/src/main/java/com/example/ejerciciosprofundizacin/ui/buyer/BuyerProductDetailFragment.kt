package com.example.ejerciciosprofundizacin.ui.buyer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ejerciciosprofundizacin.MainActivity
import com.example.ejerciciosprofundizacin.data.AppDataRepository
import com.example.ejerciciosprofundizacin.databinding.FragmentBuyerProductDetailBinding
import com.example.ejerciciosprofundizacin.model.Product

class BuyerProductDetailFragment : Fragment() {

    private var _binding: FragmentBuyerProductDetailBinding? = null
    private val binding get() = _binding!!
    private var product: Product? = null
    private var quantity: Int = 1

    companion object {
        private const val ARG_PRODUCT_ID = "arg_product_id"

        fun newInstance(productId: String): BuyerProductDetailFragment {
            val fragment = BuyerProductDetailFragment()
            val args = Bundle()
            args.putString(ARG_PRODUCT_ID, productId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuyerProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productId = arguments?.getString(ARG_PRODUCT_ID) ?: "prod_001"
        product = AppDataRepository.getProductById(productId) ?: AppDataRepository.getProducts().first()

        populateProductData(product!!)

        binding.btnBackDetail.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnDetailMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.tvDetailQuantity.text = quantity.toString()
            }
        }

        binding.btnDetailPlus.setOnClickListener {
            if (quantity < (product?.stock ?: 10)) {
                quantity++
                binding.tvDetailQuantity.text = quantity.toString()
            } else {
                Toast.makeText(requireContext(), "Límite de existencias alcanzado", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDetailAddToCart.setOnClickListener {
            product?.let { p ->
                AppDataRepository.addToCart(p, quantity)
                (activity as? MainActivity)?.updateCartBadge()
                Toast.makeText(requireContext(), "Se agregaron $quantity unidades al carrito", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDetailBuyNow.setOnClickListener {
            product?.let { p ->
                AppDataRepository.addToCart(p, quantity)
                (activity as? MainActivity)?.updateCartBadge()
                (activity as? MainActivity)?.loadFragment(BuyerCheckoutFragment(), "Pasarela de Pagos")
            }
        }
    }

    private fun populateProductData(p: Product) {
        binding.tvDetailCategoryBadge.text = p.category.uppercase()
        binding.tvDetailName.text = p.name
        binding.tvDetailSeller.text = "Vendido y distribuido por ${p.sellerName}"
        binding.tvDetailPrice.text = AppDataRepository.formatCOP(p.price)
        binding.tvDetailStock.text = "✓ Disponible: ${p.stock} unidades en bodega"
        binding.tvDetailDescription.text = p.description
        binding.tvDetailMaterial.text = "• Material: ${p.material}"
        binding.tvDetailDimensions.text = "• Dimensiones: ${p.dimensions}"
        binding.tvDetailWeightCapacity.text = "• Resistencia: ${p.weightCapacity}"
        binding.tvDetailQuantity.text = quantity.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
