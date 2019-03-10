package com.ujujzk.tryworkmanager.workers

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.ujujzk.tryworkmanager.MainActivity
import java.io.FileNotFoundException
import java.lang.RuntimeException
import java.nio.charset.Charset
import java.util.*


class ParseDictionaryWorker(
    appContext: Context,
    workerParams: WorkerParameters
): Worker(appContext, workerParams) {

    companion object {
        const val DICTIONARY_NAME_SEARCHING_ROW_NUMBER = 10
        const val DICTIONARY_NAME_TAG_IN_FILE = "#NAME"

        val ACCESS_TAG = ParseDictionaryWorker::class.java.simpleName

        const val KEY_DIC_URI = "KEY_IMAGE_URI_4566977"
        const val KEY_DIC_NAME = "KEY_DIC_NAME_4397733"
    }

    override fun doWork(): Result {
        Log.w("TAG", "Parse starts")
        val dicPath = inputData.getString(KEY_DIC_URI) ?: ""
        if (dicPath.isBlank()) {
            throw IllegalArgumentException("Invalid input path")
        }

        return try {
            val result = parseDicFile(Uri.parse(dicPath))
            Result.success(result)
        } catch (fnfe: FileNotFoundException) {
            Log.e("TAG", "Failed to decode input stream", fnfe)
            throw RuntimeException("Failed to decode input stream", fnfe)
        } catch (ex: Throwable){
            Log.e("TAG", "Error applying blur", ex)
            Result.failure()
        }
    }

    @Throws(FileNotFoundException::class, IllegalArgumentException::class)
    private fun parseDicFile(dicUri: Uri) : Data {
        val scanner = Scanner(applicationContext.contentResolver.openInputStream(dicUri), Charset.forName("UTF-16").name())
        var line: String
        var dicName = "Non"
        for (i in 0..DICTIONARY_NAME_SEARCHING_ROW_NUMBER){
            if (scanner.hasNext()){
                line = scanner.nextLine().trim()
                Log.w("TAG", "Line $i is $line")
                if (line.startsWith(DICTIONARY_NAME_TAG_IN_FILE)) {
                    dicName = line.replace(DICTIONARY_NAME_TAG_IN_FILE, "").replace("\"", "").trim()
                    break
                }
            } else {
                Log.w("TAG", "Can't find name on $dicUri")
                break
            }
        }
        scanner.close()

        return workDataOf(KEY_DIC_NAME to dicName)
    }
}