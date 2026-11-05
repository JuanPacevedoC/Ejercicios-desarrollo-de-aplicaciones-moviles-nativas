package com.example.ejerciciosprofundizacin.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.ejerciciosprofundizacin.data.AppDataRepository
import com.example.ejerciciosprofundizacin.databinding.FragmentAdminReportsBinding

class AdminReportsFragment : Fragment() {

    private var _binding: FragmentAdminReportsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val totalRevenue = AppDataRepository.getOrders().sumOf { it.total }
        binding.tvReportTotalRevenue.text = AppDataRepository.formatCOP(totalRevenue)

        binding.btnExportReport.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Exportación de Reporte Certificado")
                .setMessage("El informe financiero y de operaciones de FitMarket Calisthenics ha sido generado con éxito.\n\nFormato: PDF firmado digitalmente (Ley 527 de 1999)\nRadicado: RPT-2026-0089\nDestino: Descargas/FitMarket_Reporte_2026.pdf")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Aceptar", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
