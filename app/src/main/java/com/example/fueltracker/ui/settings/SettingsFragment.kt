package com.example.fueltracker.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.fueltracker.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    private val pickFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.importJson(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etCarModel.setText(viewModel.getCarModel())
        binding.etEngine.setText(viewModel.getEngine())
        binding.etGboType.setText(viewModel.getGboType())

        binding.btnExportCsv.setOnClickListener {
            viewModel.exportCsv()
        }

        binding.btnExportJson.setOnClickListener {
            viewModel.exportJson()
        }

        binding.btnImportJson.setOnClickListener {
            pickFile.launch("application/json")
        }

        viewModel.statusMessage.observe(viewLifecycleOwner) { message ->
            binding.tvStatus.text = message
            binding.tvStatus.visibility = View.VISIBLE
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveCarInfo(
            binding.etCarModel.text.toString(),
            binding.etEngine.text.toString(),
            binding.etGboType.text.toString()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
