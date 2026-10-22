package com.example.fueltracker.util

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.fueltracker.data.RefuelEntity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

object CsvExporter {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun export(context: Context, records: List<RefuelEntity>): String? {
        val csv = StringBuilder()
        csv.appendLine("Date,Type,Odometer,Liters,TotalCost,PricePerLiter,FullTank,Note")

        records.sortedBy { it.date }.forEach { r ->
            val dateStr = dateFormat.format(Date(r.date))
            val price = String.format(Locale.US, "%.2f", r.pricePerLiter())
            csv.appendLine("$dateStr,${r.fuelType},${r.odometer},${r.liters},${r.totalCost},$price,${r.isFullTank},${r.note ?: ""}")
        }

        val fileName = "fuel_tracker_export_${System.currentTimeMillis()}.csv"

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveViaMediaStore(context, fileName, csv.toString())
        } else {
            saveToDownloadsLegacy(fileName, csv.toString())
        }
    }

    private fun saveViaMediaStore(context: Context, fileName: String, content: String): String? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
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
