package com.example.fueltracker.model

enum class FuelType(val displayName: String) {
    GAS("Газ (пропан)"),
    PETROL_92("Бензин 92"),
    PETROL_95("Бензин 95"),
    PETROL_98("Бензин 98");

    companion object {
        fun fromDisplayName(name: String): FuelType {
            return entries.find { it.displayName == name } ?: GAS
        }
    }
}
