package com.example.fueltracker.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.fueltracker.data.RefuelEntity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.*

object JsonExporter {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    data class ExportData(
        val version: Int = 1,
        val appName: String = "FuelTracker",
        val records: List<RefuelEntity>
    )

    fun export(context: Context, records: List<RefuelEntity>): String? {
        val data = ExportData(records = records)
        val json = gson.toJson(data)
        val fileName = "fuel_tracker_backup_${System.currentTimeMillis()}.json"

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveViaMediaStore(context, fileName, json)
        } else {
            saveToDownloadsLegacy(fileName, json)
        }
    }

    fun import(context: Context, uri: Uri): List<RefuelEntity>? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val reader = BufferedReader(InputStreamReader(stream))
                val json = reader.readText()
                val type = object : TypeToken<ExportData>() {}.type
                val data = gson.fromJson<ExportData>(json, type)
                data?.records
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveViaMediaStore(context: Context, fileName: String, content: String): String? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "application/json")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        return try {
            uri?.let {
                resolver.openOutputStream(it)?.use { os ->
                    OutputStreamWriter(os).use { writer ->
                        writer.write(content)
                    }
                }
                "Downloads/$fileName"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveToDownloadsLegacy(fileName: String, content: String): String? {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        return try {
            FileOutputStream(file).use { fos ->
                OutputStreamWriter(fos).use { writer ->
                    writer.write(content)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
