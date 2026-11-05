package com.example.ejerciciosprofundizacin.ui.admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ejerciciosprofundizacin.R
import com.example.ejerciciosprofundizacin.data.AppDataRepository
import com.example.ejerciciosprofundizacin.databinding.FragmentAdminUsersBinding
import com.example.ejerciciosprofundizacin.model.User
import com.example.ejerciciosprofundizacin.model.UserRole
import com.example.ejerciciosprofundizacin.ui.adapter.AdminUsersAdapter
import java.util.UUID

class AdminUsersFragment : Fragment() {

    private var _binding: FragmentAdminUsersBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdminUsersAdapter

    private var currentFilterRole: UserRole? = null
    private var searchQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchAndFilter()

        binding.fabAddUser.setOnClickListener {
            showAddUserDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = AdminUsersAdapter(
            users = getFilteredUsers(),
            onEditClick = { user -> showEditUserDialog(user) },
            onToggleStatusClick = { user ->
                AppDataRepository.toggleUserStatus(user.id)
                refreshList()
                Toast.makeText(requireContext(), "Estado de ${user.name} actualizado: ${user.status}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { user ->
                showDeleteConfirmationDialog(user)
            }
        )
        binding.rvAdminUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAdminUsers.adapter = adapter
    }

    private fun setupSearchAndFilter() {
        binding.etUserSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString()?.trim() ?: ""
                refreshList()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.chipGroupUserFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            currentFilterRole = when {
                checkedIds.contains(R.id.chipFilterBuyer) -> UserRole.BUYER
                checkedIds.contains(R.id.chipFilterVendor) -> UserRole.VENDOR
                checkedIds.contains(R.id.chipFilterAdmin) -> UserRole.ADMIN
                else -> null
            }
            refreshList()
        }
    }

    private fun getFilteredUsers(): List<User> {
        return AppDataRepository.getUsers().filter { user ->
            val matchesRole = currentFilterRole == null || user.role == currentFilterRole
            val matchesSearch = searchQuery.isEmpty() ||
                    user.name.contains(searchQuery, ignoreCase = true) ||
                    user.email.contains(searchQuery, ignoreCase = true)
            matchesRole && matchesSearch
        }
    }

    private fun refreshList() {
        adapter.updateData(getFilteredUsers())
    }

    private fun showAddUserDialog() {
        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 16)
        }

        val etName = EditText(context).apply { hint = "Nombre completo" }
        val etEmail = EditText(context).apply { hint = "Correo electrónico" }
        val etPhone = EditText(context).apply { hint = "Teléfono móvil (+57)" }
        val etAddress = EditText(context).apply { hint = "Dirección y ciudad" }

        val rgRole = RadioGroup(context)
        val rbBuyer = android.widget.RadioButton(context).apply { text = "Comprador"; isChecked = true; id = View.generateViewId() }
        val rbVendor = android.widget.RadioButton(context).apply { text = "Vendedor"; id = View.generateViewId() }
        val rbAdmin = android.widget.RadioButton(context).apply { text = "Administrador"; id = View.generateViewId() }
        rgRole.addView(rbBuyer)
        rgRole.addView(rbVendor)
        rgRole.addView(rbAdmin)

        layout.addView(etName)
        layout.addView(etEmail)
        layout.addView(etPhone)
        layout.addView(etAddress)
        layout.addView(rgRole)

        AlertDialog.Builder(context)
            .setTitle("Crear Nuevo Usuario")
            .setView(layout)
            .setPositiveButton("Crear") { _, _ ->
                val name = etName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                if (name.isNotEmpty() && email.isNotEmpty()) {
                    val role = when (rgRole.checkedRadioButtonId) {
                        rbAdmin.id -> UserRole.ADMIN
                        rbVendor.id -> UserRole.VENDOR
                        else -> UserRole.BUYER
                    }
                    val newUser = User(
                        id = "usr_" + UUID.randomUUID().toString().take(6),
                        name = name,
                        email = email,
                        role = role,
                        phone = etPhone.text.toString().trim().ifEmpty { "+57 300 000 0000" },
                        address = etAddress.text.toString().trim().ifEmpty { "Bogotá, Colombia" }
                    )
                    AppDataRepository.addUser(newUser)
                    refreshList()
                    Toast.makeText(context, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "El nombre y el correo son obligatorios", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEditUserDialog(user: User) {
        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 16)
        }

        val etName = EditText(context).apply { setText(user.name); hint = "Nombre completo" }
        val etPhone = EditText(context).apply { setText(user.phone); hint = "Teléfono" }
        val etAddress = EditText(context).apply { setText(user.address); hint = "Dirección" }

        layout.addView(etName)
        layout.addView(etPhone)
        layout.addView(etAddress)

        AlertDialog.Builder(context)
            .setTitle("Editar Usuario: ${user.name}")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                user.name = etName.text.toString().trim()
                user.phone = etPhone.text.toString().trim()
                user.address = etAddress.text.toString().trim()
                AppDataRepository.updateUser(user)
                refreshList()
                Toast.makeText(context, "Usuario modificado correctamente", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDeleteConfirmationDialog(user: User) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Usuario")
            .setMessage("¿Estás seguro de que deseas eliminar permanentemente a '${user.name}' (${user.email})?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Eliminar") { _, _ ->
                AppDataRepository.deleteUser(user.id)
                refreshList()
                Toast.makeText(requireContext(), "Usuario eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
