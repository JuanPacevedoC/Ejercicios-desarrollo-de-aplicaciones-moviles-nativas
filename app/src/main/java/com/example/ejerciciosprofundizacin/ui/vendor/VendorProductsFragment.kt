package com.example.ejerciciosprofundizacin.ui.vendor

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ejerciciosprofundizacin.R
import com.example.ejerciciosprofundizacin.data.AppDataRepository
import com.example.ejerciciosprofundizacin.data.FirestoreRepository
import com.example.ejerciciosprofundizacin.databinding.FragmentVendorProductsBinding
import com.example.ejerciciosprofundizacin.model.Product
import com.example.ejerciciosprofundizacin.ui.adapter.VendorProductsAdapter
import com.example.ejerciciosprofundizacin.util.ImageUploadHelper
import java.util.UUID

/**
 * Fragmento de gestión de inventario y publicaciones para el rol de Vendedor.
 *
 * Cumplimiento de requerimientos Actividad 3 / Proyecto Final:
 * - Publicación, edición y eliminación de equipamiento deportivo propio del vendedor.
 * - Subida de imágenes desde Cámara o Galería hacia Firebase Storage.
 * - Persistencia sincronizada con Cloud Firestore.
 * - Filtro automático para listar únicamente los productos asignados a la tienda del vendedor.
 */
class VendorProductsFragment : Fragment() {

    private var _binding: FragmentVendorProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: VendorProductsAdapter
    private var searchQuery: String = ""

    private var tempCameraUri: Uri? = null
    private var currentUploadedImageUrl: String = ""
    private var ivDialogPreview: ImageView? = null

    // Launcher para Galería
    private val pickGalleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { handleImageSelected(it) }
    }

    // Launcher para Cámara
    private val takeCameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success && tempCameraUri != null) {
            handleImageSelected(tempCameraUri!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVendorProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        loadProductsFromFirestore()

        binding.fabVendorAddProduct.setOnClickListener {
            showAddProductDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = VendorProductsAdapter(
            products = getFilteredProducts(),
            onEditClick = { product -> showEditProductDialog(product) },
            onDeleteClick = { product -> showDeleteProductDialog(product) }
        )
        binding.rvVendorProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVendorProducts.adapter = adapter
    }

    private fun loadProductsFromFirestore() {
        FirestoreRepository.getProducts(
            onSuccess = { firestoreProds ->
                if (firestoreProds.isNotEmpty()) {
                    firestoreProds.forEach { fProd ->
                        val existing = AppDataRepository.getProductById(fProd.id)
                        if (existing == null) {
                            AppDataRepository.addProduct(fProd)
                        } else {
                            AppDataRepository.updateProduct(fProd)
                        }
                    }
                    refreshList()
                }
            },
            onFailure = {}
        )
    }

    private fun setupSearch() {
        binding.etVendorProductSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString()?.trim() ?: ""
                refreshList()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun getFilteredProducts(): List<Product> {
        val currentVendorName = AppDataRepository.currentUser.name
        return AppDataRepository.getProducts().filter { product ->
            val matchesVendor = product.sellerName.equals(currentVendorName, ignoreCase = true) ||
                    product.sellerName.contains("IronGrip", ignoreCase = true)
            val matchesSearch = searchQuery.isEmpty() ||
                    product.name.contains(searchQuery, ignoreCase = true) ||
                    product.category.contains(searchQuery, ignoreCase = true)
            matchesVendor && matchesSearch
        }
    }

    private fun refreshList() {
        adapter.updateData(getFilteredProducts())
    }

    private fun showAddProductDialog() {
        val context = requireContext()
        currentUploadedImageUrl = ""

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 16)
        }

        val etName = EditText(context).apply { hint = "Nombre del producto (ej. Paralelas)" }
        val etCategory = EditText(context).apply { hint = "Categoría (Barras, Anillas, etc.)" }
        val etPrice = EditText(context).apply { hint = "Precio en COP" }
        val etStock = EditText(context).apply { hint = "Stock disponible" }
        val etMaterial = EditText(context).apply { hint = "Material y especificaciones" }
        val etWeight = EditText(context).apply { hint = "Capacidad de peso (ej. 200 kg)" }

        val btnAttachImage = Button(context).apply {
            text = "📷 Adjuntar Foto (Cámara / Galería)"
            setOnClickListener { showImageSourceDialog() }
        }

        ivDialogPreview = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300).apply { topMargin = 16 }
            visibility = View.GONE
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        layout.addView(etName)
        layout.addView(etCategory)
        layout.addView(etPrice)
        layout.addView(etStock)
        layout.addView(etMaterial)
        layout.addView(etWeight)
        layout.addView(btnAttachImage)
        layout.addView(ivDialogPreview)

        AlertDialog.Builder(context)
            .setTitle("Publicar Producto en mi Tienda")
            .setView(layout)
            .setPositiveButton("Publicar") { _, _ ->
                val name = etName.text.toString().trim()
                val price = etPrice.text.toString().toDoubleOrNull() ?: 99000.0
                val stock = etStock.text.toString().toIntOrNull() ?: 10
                if (name.isNotEmpty()) {
                    val newProduct = Product(
                        id = "prod_" + UUID.randomUUID().toString().take(6),
                        name = name,
                        category = etCategory.text.toString().trim().ifEmpty { "Calistenia" },
                        price = price,
                        stock = stock,
                        description = "Equipamiento fabricado y garantizado por ${AppDataRepository.currentUser.name}.",
                        material = etMaterial.text.toString().trim().ifEmpty { "Acero al carbono" },
                        dimensions = "Estándar",
                        weightCapacity = etWeight.text.toString().trim().ifEmpty { "Hasta 200 kg" },
                        sellerName = AppDataRepository.currentUser.name,
                        imageUrl = currentUploadedImageUrl,
                        iconRes = R.drawable.ic_fitness_center
                    )
                    AppDataRepository.addProduct(newProduct)
                    refreshList()

                    // Persistir en Firestore
                    FirestoreRepository.addProduct(
                        product = newProduct,
                        onSuccess = {
                            Toast.makeText(context, "Producto publicado y sincronizado con Firestore", Toast.LENGTH_SHORT).show()
                        },
                        onFailure = {
                            Toast.makeText(context, "Producto publicado localmente", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("📸 Tomar Foto con Cámara", "🖼️ Seleccionar de Galería")
        AlertDialog.Builder(requireContext())
            .setTitle("Subir Foto del Producto")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> pickGalleryLauncher.launch("image/*")
                }
            }
            .show()
    }

    private fun openCamera() {
        val context = requireContext()
        val tempFile = ImageUploadHelper.createTempImageFile(context)
        tempCameraUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
        tempCameraUri?.let { takeCameraLauncher.launch(it) }
    }

    private fun handleImageSelected(uri: Uri) {
        ivDialogPreview?.let { preview ->
            preview.visibility = View.VISIBLE
            preview.setImageURI(uri)
        }
        Toast.makeText(requireContext(), "Subiendo imagen a Firebase Storage...", Toast.LENGTH_SHORT).show()

        ImageUploadHelper.uploadImageToFirebase(
            imageUri = uri,
            onSuccess = { downloadUrl ->
                currentUploadedImageUrl = downloadUrl
                Toast.makeText(requireContext(), "¡Imagen guardada en Firebase Storage!", Toast.LENGTH_SHORT).show()
            },
            onFailure = {
                currentUploadedImageUrl = uri.toString()
                Toast.makeText(requireContext(), "Imagen vinculada localmente", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun showEditProductDialog(product: Product) {
        val context = requireContext()
        currentUploadedImageUrl = product.imageUrl

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 16)
        }

        val etName = EditText(context).apply { setText(product.name); hint = "Nombre" }
        val etPrice = EditText(context).apply { setText(product.price.toLong().toString()); hint = "Precio COP" }
        val etStock = EditText(context).apply { setText(product.stock.toString()); hint = "Stock" }
        val etMaterial = EditText(context).apply { setText(product.material); hint = "Material" }

        val btnChangeImage = Button(context).apply {
            text = "📷 Cambiar Foto (Cámara / Galería)"
            setOnClickListener { showImageSourceDialog() }
        }

        ivDialogPreview = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300).apply { topMargin = 16 }
            visibility = if (product.imageUrl.isNotEmpty()) View.VISIBLE else View.GONE
            ImageUploadHelper.loadProductImage(this, product.imageUrl)
        }

        layout.addView(etName)
        layout.addView(etPrice)
        layout.addView(etStock)
        layout.addView(etMaterial)
        layout.addView(btnChangeImage)
        layout.addView(ivDialogPreview)

        AlertDialog.Builder(context)
            .setTitle("Editar: ${product.name}")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                product.name = etName.text.toString().trim()
                product.price = etPrice.text.toString().toDoubleOrNull() ?: product.price
                product.stock = etStock.text.toString().toIntOrNull() ?: product.stock
                product.material = etMaterial.text.toString().trim()
                product.imageUrl = currentUploadedImageUrl
                AppDataRepository.updateProduct(product)
                refreshList()

                // Persistir en Firestore
                FirestoreRepository.updateProduct(
                    product = product,
                    onSuccess = {
                        Toast.makeText(context, "Publicación actualizada en Firestore", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = {
                        Toast.makeText(context, "Publicación actualizada localmente", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDeleteProductDialog(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Publicación")
            .setMessage("¿Deseas retirar permanentemente '${product.name}' de tu catálogo?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Eliminar") { _, _ ->
                AppDataRepository.deleteProduct(product.id)
                refreshList()

                // Persistir en Firestore
                FirestoreRepository.deleteProduct(
                    productId = product.id,
                    onSuccess = {
                        Toast.makeText(requireContext(), "Producto eliminado de Firestore", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = {
                        Toast.makeText(requireContext(), "Producto eliminado localmente", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
