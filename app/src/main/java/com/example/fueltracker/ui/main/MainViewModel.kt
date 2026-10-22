package com.example.fueltracker.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fueltracker.data.AppDatabase
import com.example.fueltracker.data.RefuelEntity
import com.example.fueltracker.data.RefuelRepository
import com.example.fueltracker.util.ConsumptionCalculator
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RefuelRepository
    val allRecords: LiveData<List<RefuelEntity>>

    private val _statistics = MutableLiveData<ConsumptionCalculator.Statistics>()
    val statistics: LiveData<ConsumptionCalculator.Statistics> = _statistics

    init {
        val dao = AppDatabase.getDatabase(application).refuelDao()
        repository = RefuelRepository(dao)
        allRecords = repository.allRecords

        allRecords.observeForever { records ->
            _statistics.value = ConsumptionCalculator.calculate(records)
        }
    }

    fun delete(record: RefuelEntity) = viewModelScope.launch {
        repository.delete(record)
    }
}
