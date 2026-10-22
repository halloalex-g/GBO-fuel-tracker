package com.example.fueltracker.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fueltracker.R
import com.example.fueltracker.data.RefuelEntity
import com.example.fueltracker.databinding.ItemRefuelBinding
import com.example.fueltracker.model.FuelType
import java.text.SimpleDateFormat
import java.util.*

class RefuelAdapter(
    private val onItemClick: (RefuelEntity) -> Unit
) : ListAdapter<RefuelEntity, RefuelAdapter.ViewHolder>(DiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRefuelBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemRefuelBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RefuelEntity) {
            binding.tvFuelType.text = item.fuelType
            binding.tvDate.text = dateFormat.format(Date(item.date))
            binding.tvOdometer.text = "${item.odometer} км"
            binding.tvVolumeCost.text = "${item.liters} л · ${item.totalCost.toInt()} ₽"
            binding.ivFullTank.visibility = if (item.isFullTank) View.VISIBLE else View.GONE

            val colorRes = when (item.getFuelTypeEnum()) {
                FuelType.GAS -> R.color.gas_green
                else -> R.color.petrol_orange
            }
            binding.colorIndicator.setBackgroundResource(colorRes)

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<RefuelEntity>() {
        override fun areItemsTheSame(oldItem: RefuelEntity, newItem: RefuelEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: RefuelEntity, newItem: RefuelEntity) =
            oldItem == newItem
    }
}
