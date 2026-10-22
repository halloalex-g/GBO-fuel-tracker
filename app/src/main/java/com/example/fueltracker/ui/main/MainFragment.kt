package com.example.fueltracker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fueltracker.R
import com.example.fueltracker.databinding.FragmentMainBinding
import com.example.fueltracker.util.ConsumptionCalculator

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: RefuelAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RefuelAdapter { record ->
            val action = MainFragmentDirections.actionMainToDetail(record.id)
            findNavController().navigate(action)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.allRecords.observe(viewLifecycleOwner) { records ->
            if (records.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                adapter.submitList(records)
            }
        }

        viewModel.statistics.observe(viewLifecycleOwner) { stats ->
            updateStatistics(stats)
        }
    }

    private fun updateStatistics(stats: ConsumptionCalculator.Statistics) {
        binding.tvStatMileage.text = "${stats.totalMileage} км"
        binding.tvStatGas.text = ConsumptionCalculator.formatConsumption(stats.avgGasConsumption)
        binding.tvStatPetrol.text = ConsumptionCalculator.formatConsumption(stats.avgPetrolConsumption)
        binding.tvStatSpent.text = ConsumptionCalculator.formatCurrency(stats.totalSpent)
        binding.tvStatCostKm.text = ConsumptionCalculator.formatCostPerKm(stats.costPerKm)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
