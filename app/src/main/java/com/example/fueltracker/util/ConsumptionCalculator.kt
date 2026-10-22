package com.example.fueltracker.util

import com.example.fueltracker.data.RefuelEntity
import com.example.fueltracker.model.FuelType

object ConsumptionCalculator {

    data class Statistics(
        val totalMileage: Int,
        val avgGasConsumption: Double,
        val avgPetrolConsumption: Double,
        val totalSpent: Double,
        val costPerKm: Double,
        val gasRecordsCount: Int,
        val petrolRecordsCount: Int
    )

    fun calculate(records: List<RefuelEntity>): Statistics {
        if (records.size < 2) {
            return Statistics(
                totalMileage = 0,
                avgGasConsumption = 0.0,
                avgPetrolConsumption = 0.0,
                totalSpent = records.sumOf { it.totalCost },
                costPerKm = 0.0,
                gasRecordsCount = records.count { it.getFuelTypeEnum() == FuelType.GAS },
                petrolRecordsCount = records.count { it.getFuelTypeEnum() != FuelType.GAS }
            )
        }

        val sorted = records.sortedBy { it.odometer }
        val minOdo = sorted.first().odometer
        val maxOdo = sorted.last().odometer
        val totalMileage = maxOdo - minOdo
        val totalSpent = records.sumOf { it.totalCost }
        val costPerKm = if (totalMileage > 0) totalSpent / totalMileage else 0.0

        val gasConsumption = calculateForFuelType(sorted, FuelType.GAS)
        val petrolConsumption = calculateForFuelType(sorted.filter { it.getFuelTypeEnum() != FuelType.GAS }, null)

        return Statistics(
            totalMileage = totalMileage,
            avgGasConsumption = gasConsumption,
            avgPetrolConsumption = petrolConsumption,
            totalSpent = totalSpent,
            costPerKm = costPerKm,
            gasRecordsCount = records.count { it.getFuelTypeEnum() == FuelType.GAS },
            petrolRecordsCount = records.count { it.getFuelTypeEnum() != FuelType.GAS }
        )
    }

    private fun calculateForFuelType(records: List<RefuelEntity>, targetType: FuelType?): Double {
        val filtered = if (targetType != null) {
            records.filter { it.getFuelTypeEnum() == targetType && it.isFullTank }
        } else {
            records.filter { it.getFuelTypeEnum() != FuelType.GAS && it.isFullTank }
        }

        if (filtered.size < 2) return 0.0

        val sorted = filtered.sortedBy { it.odometer }
        var totalConsumption = 0.0
        var intervals = 0

        for (i in 1 until sorted.size) {
            val prev = sorted[i - 1]
            val curr = sorted[i]
            val distance = curr.odometer - prev.odometer
            if (distance > 0) {
                val consumption = (prev.liters / distance) * 100.0
                totalConsumption += consumption
                intervals++
            }
        }

        return if (intervals > 0) totalConsumption / intervals else 0.0
    }

    fun formatConsumption(value: Double): String {
        return String.format("%.1f л/100км", value)
    }

    fun formatCurrency(value: Double): String {
        return String.format("%.2f ₽", value)
    }

    fun formatCostPerKm(value: Double): String {
        return String.format("%.2f ₽/км", value)
    }
}
