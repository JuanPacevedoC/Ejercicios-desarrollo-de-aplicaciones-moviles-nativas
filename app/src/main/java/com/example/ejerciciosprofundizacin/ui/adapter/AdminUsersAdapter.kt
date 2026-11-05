package com.example.ejerciciosprofundizacin.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ejerciciosprofundizacin.R
import com.example.ejerciciosprofundizacin.databinding.ItemAdminUserBinding
import com.example.ejerciciosprofundizacin.model.User
import com.example.ejerciciosprofundizacin.model.UserRole

class AdminUsersAdapter(
    private var users: List<User>,
    private val onEditClick: (User) -> Unit,
    private val onToggleStatusClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit
) : RecyclerView.Adapter<AdminUsersAdapter.UserViewHolder>() {

    fun updateData(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemAdminUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    inner class UserViewHolder(private val binding: ItemAdminUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.tvUserName.text = user.name
            binding.tvUserEmail.text = user.email
            binding.tvUserPhone.text = "Tel: ${user.phone} • ${user.address}"
            binding.tvUserRoleBadge.text = user.role.displayName.uppercase()

            val context = binding.root.context
            when (user.role) {
                UserRole.ADMIN -> {
                    binding.tvUserRoleBadge.setTextColor(ContextCompat.getColor(context, R.color.roleAdmin))
                    binding.ivUserAvatar.setColorFilter(ContextCompat.getColor(context, R.color.roleAdmin))
                }
                UserRole.VENDOR -> {
                    binding.tvUserRoleBadge.setTextColor(ContextCompat.getColor(context, R.color.roleVendor))
                    binding.ivUserAvatar.setColorFilter(ContextCompat.getColor(context, R.color.roleVendor))
                }
                UserRole.BUYER -> {
                    binding.tvUserRoleBadge.setTextColor(ContextCompat.getColor(context, R.color.roleBuyer))
                    binding.ivUserAvatar.setColorFilter(ContextCompat.getColor(context, R.color.roleBuyer))
                }
            }

            if (user.status == "Activo") {
                binding.tvUserStatusBadge.text = "• Activo"
                binding.tvUserStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.statusDelivered))
                binding.btnToggleStatus.text = "Suspender"
                binding.btnToggleStatus.setTextColor(ContextCompat.getColor(context, R.color.statusPending))
            } else {
                binding.tvUserStatusBadge.text = "• Suspendido"
                binding.tvUserStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.statusCancelled))
                binding.btnToggleStatus.text = "Activar"
                binding.btnToggleStatus.setTextColor(ContextCompat.getColor(context, R.color.statusDelivered))
            }

            binding.btnEditUser.setOnClickListener { onEditClick(user) }
            binding.btnToggleStatus.setOnClickListener { onToggleStatusClick(user) }
            binding.btnDeleteUser.setOnClickListener { onDeleteClick(user) }
        }
    }
}
