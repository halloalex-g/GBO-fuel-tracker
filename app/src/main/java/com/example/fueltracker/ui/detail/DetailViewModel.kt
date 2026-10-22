package com.example.fueltracker.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fueltracker.data.AppDatabase
import com.example.fueltracker.data.RefuelEntity
import com.example.fueltracker.data.RefuelRepository
import kotlinx.coroutines.launch

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RefuelRepository

    private val _record = MutableLiveData<RefuelEntity?>()
    val record: LiveData<RefuelEntity?> = _record

    private val _deleteSuccess = MutableLiveData<Boolean>()
    val deleteSuccess: LiveData<Boolean> = _deleteSuccess

    init {
        val dao = AppDatabase.getDatabase(application).refuelDao()
        repository = RefuelRepository(dao)
    }

    fun loadRecord(id: Long) = viewModelScope.launch {
        val records = repository.getAllList()
        _record.value = records.find { it.id == id }
    }

    fun delete(record: RefuelEntity) = viewModelScope.launch {
        repository.delete(record)
        _deleteSuccess.value = true
    }
}
