package com.example.fueltracker.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RefuelDao {

    @Query("SELECT * FROM refuel_records ORDER BY date DESC")
    fun getAll(): LiveData<List<RefuelEntity>>

    @Query("SELECT * FROM refuel_records ORDER BY date DESC")
    suspend fun getAllList(): List<RefuelEntity>

    @Query("SELECT * FROM refuel_records WHERE fuelType = :fuelType ORDER BY date DESC")
    suspend fun getByFuelType(fuelType: String): List<RefuelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: RefuelEntity): Long

    @Update
    suspend fun update(record: RefuelEntity)

    @Delete
    suspend fun delete(record: RefuelEntity)

    @Query("SELECT * FROM refuel_records WHERE date = :date AND odometer = :odometer LIMIT 1")
    suspend fun findByDateAndOdometer(date: Long, odometer: Int): RefuelEntity?
}
