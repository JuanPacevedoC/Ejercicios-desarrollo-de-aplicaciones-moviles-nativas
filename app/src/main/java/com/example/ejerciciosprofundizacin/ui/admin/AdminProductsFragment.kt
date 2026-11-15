package com.example.ejerciciosprofundizacin.ui.admin

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
import com.example.ejerciciosprofundizacin.databinding.FragmentAdminProductsBinding
import com.example.ejerciciosprofundizacin.model.Product
import com.example.ejerciciosprofundizacin.ui.adapter.AdminProductsAdapter
import com.example.ejerciciosprofundizacin.util.ImageUploadHelper
import java.io.File
import java.util.UUID

class AdminProductsFragment : Fragment() {

    private var _binding: FragmentAdminProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdminProductsAdapter
    private var searchQuery: String = ""

    // Variables para captura y carga de fotos
    private var tempCameraUri: Uri? = null
    private var currentUploadedImageUrl: String = ""
    private var ivDialogPreview: ImageView? = null

    // Launcher para Galería
    private val pickGalleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            handleImageSelected(it)
        }
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
        _binding = FragmentAdminProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        loadProductsFromFirestore()

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
            onFailure = {
                // Fallback a productos locales
            }
        )
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
        currentUploadedImageUrl = ""

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

        // Botón y preview de imagen
        val btnAttachImage = Button(context).apply {
            text = "📷 Adjuntar Foto (Cámara / Galería)"
            setOnClickListener {
                showImageSourceDialog()
            }
        }

        ivDialogPreview = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                300
            ).apply { topMargin = 16 }
            visibility = View.GONE
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        layout.addView(etName)
        layout.addView(etCategory)
        layout.addView(etPrice)
        layout.addView(etStock)
        layout.addView(etMaterial)
        layout.addView(etSeller)
        layout.addView(btnAttachImage)
        layout.addView(ivDialogPreview)

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
                        imageUrl = currentUploadedImageUrl,
                        iconRes = R.drawable.ic_fitness_center
                    )
                    AppDataRepository.addProduct(newProd)
                    refreshList()

                    // Persistir en Firestore
                    FirestoreRepository.addProduct(
                        product = newProd,
                        onSuccess = {
                            Toast.makeText(context, "Producto guardado y persistido en Firestore", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(), "¡Imagen almacenada exitosamente en Firebase Storage!", Toast.LENGTH_SHORT).show()
            },
            onFailure = {
                // Si Firebase Storage falla sin google-services.json, mantener uri local
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
        val etPrice = EditText(context).apply { setText(product.price.toLong().toString()); hint = "Precio" }
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
                        Toast.makeText(context, "Producto actualizado en Firestore", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = {
                        Toast.makeText(context, "Producto actualizado localmente", Toast.LENGTH_SHORT).show()
                    }
                )
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
