package com.example.fueltracker.ui.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fueltracker.data.AppDatabase
import com.example.fueltracker.data.RefuelEntity
import com.example.fueltracker.data.RefuelRepository
import kotlinx.coroutines.launch

class AddRefuelViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RefuelRepository

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    private val _existingRecord = MutableLiveData<RefuelEntity?>()
    val existingRecord: LiveData<RefuelEntity?> = _existingRecord

    init {
        val dao = AppDatabase.getDatabase(application).refuelDao()
        repository = RefuelRepository(dao)
    }

    fun loadRecord(id: Long) = viewModelScope.launch {
        if (id > 0) {
            val records = repository.getAllList()
            _existingRecord.value = records.find { it.id == id }
        }
    }

    fun save(record: RefuelEntity) = viewModelScope.launch {
        if (record.id > 0) {
            repository.update(record)
        } else {
            repository.insert(record)
        }
        _saveSuccess.value = true
    }
}
