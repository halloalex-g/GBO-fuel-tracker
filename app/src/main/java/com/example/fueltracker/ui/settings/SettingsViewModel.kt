package com.example.fueltracker.ui.settings

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fueltracker.data.AppDatabase
import com.example.fueltracker.data.RefuelEntity
import com.example.fueltracker.data.RefuelRepository
import com.example.fueltracker.util.CsvExporter
import com.example.fueltracker.util.JsonExporter
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RefuelRepository
    private val prefs = application.getSharedPreferences("car_settings", Context.MODE_PRIVATE)

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage

    init {
        val dao = AppDatabase.getDatabase(application).refuelDao()
        repository = RefuelRepository(dao)
    }

    fun getCarModel(): String = prefs.getString("car_model", "Renault Kaptur") ?: "Renault Kaptur"
    fun getEngine(): String = prefs.getString("engine", "HR16DE") ?: "HR16DE"
    fun getGboType(): String = prefs.getString("gbo_type", "Пропан (4-е поколение)") ?: "Пропан (4-е поколение)"

    fun saveCarInfo(model: String, engine: String, gboType: String) {
        prefs.edit()
            .putString("car_model", model)
            .putString("engine", engine)
            .putString("gbo_type", gboType)
            .apply()
    }

    fun exportCsv() = viewModelScope.launch {
        val records = repository.getAllList()
        val path = CsvExporter.export(getApplication(), records)
        _statusMessage.value = if (path != null) "CSV: $path" else "Ошибка экспорта CSV"
    }

    fun exportJson() = viewModelScope.launch {
        val records = repository.getAllList()
        val path = JsonExporter.export(getApplication(), records)
        _statusMessage.value = if (path != null) "JSON: $path" else "Ошибка экспорта JSON"
    }

    fun importJson(uri: Uri) = viewModelScope.launch {
        val records = JsonExporter.import(getApplication(), uri)
        if (records != null) {
            var imported = 0
            var skipped = 0
            for (record in records) {
                val existing = repository.findByDateAndOdometer(record.date, record.odometer)
                if (existing == null) {
                    repository.insert(record.copy(id = 0))
                    imported++
                } else {
                    skipped++
                }
            }
            _statusMessage.value = "Импорт: $imported новых, $skipped пропущено"
        } else {
            _statusMessage.value = "Ошибка чтения файла"
        }
    }
}
