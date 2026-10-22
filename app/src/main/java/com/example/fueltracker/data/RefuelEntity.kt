package com.example.fueltracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fueltracker.model.FuelType

@Entity(tableName = "refuel_records")
data class RefuelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val fuelType: String,
    val odometer: Int,
    val liters: Double,
    val totalCost: Double,
    val isFullTank: Boolean,
    val note: String? = null
) {
    fun getFuelTypeEnum(): FuelType = FuelType.fromDisplayName(fuelType)

    fun pricePerLiter(): Double =
        if (liters > 0) totalCost / liters else 0.0
}
