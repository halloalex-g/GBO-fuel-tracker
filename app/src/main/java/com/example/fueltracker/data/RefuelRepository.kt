package com.example.fueltracker.data

import androidx.lifecycle.LiveData

class RefuelRepository(private val dao: RefuelDao) {

    val allRecords: LiveData<List<RefuelEntity>> = dao.getAll()

    suspend fun insert(record: RefuelEntity): Long = dao.insert(record)

    suspend fun update(record: RefuelEntity) = dao.update(record)

    suspend fun delete(record: RefuelEntity) = dao.delete(record)

    suspend fun getAllList(): List<RefuelEntity> = dao.getAllList()

    suspend fun getByFuelType(fuelType: String): List<RefuelEntity> =
        dao.getByFuelType(fuelType)

    suspend fun findByDateAndOdometer(date: Long, odometer: Int): RefuelEntity? =
        dao.findByDateAndOdometer(date, odometer)
}
