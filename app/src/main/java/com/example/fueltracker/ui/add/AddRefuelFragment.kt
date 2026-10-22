package com.example.fueltracker.ui.add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.example.fueltracker.data.RefuelEntity
import com.example.fueltracker.databinding.FragmentAddRefuelBinding
import com.example.fueltracker.model.FuelType
import java.text.SimpleDateFormat
import java.util.*

class AddRefuelFragment : Fragment() {

    private var _binding: FragmentAddRefuelBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddRefuelViewModel by viewModels()
    private val args: AddRefuelFragmentArgs by navArgs()

    private var selectedDate: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRefuelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFuelTypeToggle()
        setupDatePicker()

        binding.btnSave.setOnClickListener { saveRecord() }

        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), R.string.save_success, Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }

        if (args.recordId > 0) {
            viewModel.loadRecord(args.recordId)
            viewModel.existingRecord.observe(viewLifecycleOwner) { record ->
                record?.let { fillForm(it) }
            }
        } else {
            binding.etDate.setText(dateFormat.format(selectedDate.time))
        }
    }

    private fun setupFuelTypeToggle() {
        binding.toggleFuelType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btn_gas -> {
                        binding.togglePetrolType.visibility = View.GONE
                        binding.btnGas.setStrokeColorResource(R.color.gas_green)
                        binding.btnGas.setTextColor(resources.getColor(R.color.gas_green, null))
                    }
                    R.id.btn_petrol -> {
                        binding.togglePetrolType.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    selectedDate.set(year, month, day)
                    TimePickerDialog(
                        requireContext(),
                        { _, hour, minute ->
                            selectedDate.set(Calendar.HOUR_OF_DAY, hour)
                            selectedDate.set(Calendar.MINUTE, minute)
                            binding.etDate.setText(dateFormat.format(selectedDate.time))
                        },
                        selectedDate.get(Calendar.HOUR_OF_DAY),
                        selectedDate.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun fillForm(record: RefuelEntity) {
        selectedDate.timeInMillis = record.date
        binding.etDate.setText(dateFormat.format(selectedDate.time))
        binding.etOdometer.setText(record.odometer.toString())
        binding.etLiters.setText(record.liters.toString())
        binding.etCost.setText(record.totalCost.toString())
        binding.switchFullTank.isChecked = record.isFullTank
        binding.etNote.setText(record.note ?: "")

        when (record.getFuelTypeEnum()) {
            FuelType.GAS -> {
                binding.toggleFuelType.check(R.id.btn_gas)
                binding.togglePetrolType.visibility = View.GONE
            }
            FuelType.PETROL_92 -> {
                binding.toggleFuelType.check(R.id.btn_petrol)
                binding.togglePetrolType.visibility = View.VISIBLE
                binding.togglePetrolType.check(R.id.btn_petrol_92)
            }
            FuelType.PETROL_95 -> {
                binding.toggleFuelType.check(R.id.btn_petrol)
                binding.togglePetrolType.visibility = View.VISIBLE
                binding.togglePetrolType.check(R.id.btn_petrol_95)
            }
            FuelType.PETROL_98 -> {
                binding.toggleFuelType.check(R.id.btn_petrol)
                binding.togglePetrolType.visibility = View.VISIBLE
                binding.togglePetrolType.check(R.id.btn_petrol_98)
            }
        }
    }

    private fun saveRecord() {
        val odometer = binding.etOdometer.text.toString().toIntOrNull()
        val liters = binding.etLiters.text.toString().toDoubleOrNull()
        val cost = binding.etCost.text.toString().toDoubleOrNull()

        if (odometer == null || liters == null || cost == null) {
            Toast.makeText(requireContext(), R.string.fill_all_fields, Toast.LENGTH_SHORT).show()
            return
        }

        val fuelType = when (binding.toggleFuelType.checkedButtonId) {
            R.id.btn_gas -> FuelType.GAS.displayName
            R.id.btn_petrol -> when (binding.togglePetrolType.checkedButtonId) {
                R.id.btn_petrol_95 -> FuelType.PETROL_95.displayName
                R.id.btn_petrol_98 -> FuelType.PETROL_98.displayName
                else -> FuelType.PETROL_92.displayName
            }
            else -> FuelType.GAS.displayName
        }

        val record = RefuelEntity(
           id = if (args.recordId > 0) args.recordId else 0,
            date = selectedDate.timeInMillis,
            fuelType = fuelType,
            odometer = odometer,
            liters = liters,
            totalCost = cost,
            isFullTank = binding.switchFullTank.isChecked,
            note = binding.etNote.text.toString().ifBlank { null }
        )

        viewModel.save(record)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
