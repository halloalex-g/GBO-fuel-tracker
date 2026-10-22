package com.example.fueltracker.ui.detail

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fueltracker.R
import com.example.fueltracker.databinding.FragmentDetailBinding
import java.text.SimpleDateFormat
import java.util.*

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadRecord(args.recordId)

        viewModel.record.observe(viewLifecycleOwner) { record ->
            record?.let { bindData(it) }
        }

        viewModel.deleteSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), R.string.delete_success, Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }

        binding.btnEdit.setOnClickListener {
            val record = viewModel.record.value ?: return@setOnClickListener
            val action = DetailFragmentDirections.actionDetailToAdd(record.id)
            findNavController().navigate(action)
        }

        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirm_delete)
                .setPositiveButton(R.string.label_delete) { _, _ ->
                    viewModel.record.value?.let { viewModel.delete(it) }
                }
                .setNegativeButton(R.string.label_cancel, null)
                .show()
        }
    }

    private fun bindData(record: com.example.fueltracker.data.RefuelEntity) {
        binding.tvFuelType.text = record.fuelType
        binding.tvDate.text = dateFormat.format(Date(record.date))
        binding.tvOdometer.text = "${record.odometer} км"
        binding.tvLiters.text = "${record.liters} л"
        binding.tvCost.text = "${record.totalCost} ₽"
        binding.tvPricePerLiter.text = String.format("%.2f ₽/л", record.pricePerLiter())
        binding.tvFullTank.text = if (record.isFullTank) "Да" else "Нет"

        if (record.note.isNullOrBlank()) {
            binding.tvNoteLabel.visibility = View.GONE
            binding.tvNote.visibility = View.GONE
        } else {
            binding.tvNoteLabel.visibility = View.VISIBLE
            binding.tvNote.visibility = View.VISIBLE
            binding.tvNote.text = record.note
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
