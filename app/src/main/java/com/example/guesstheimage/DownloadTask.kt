package com.example.guesstheimage

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile
import java.util.*

class DownloadTask(var downloadListener: DownloadListener, @SuppressLint("StaticFieldLeak") var context: Context) : AsyncTask<String?, Int?, Int>() {

    companion object {
        // Declare static variables to switch between download results
        private const val TYPE_SUCCESS = 0
        private const val TYPE_FAILED = 1
        private const val TYPE_PAUSED = 2
        private const val TYPE_CANCELED = 3
    }

    private val isCanceled = false
    private val isPaused = false
    private var lastProgress = 0
    
    override fun doInBackground(vararg params: String?): Int {
        var inputStream: InputStream? = null
        var accessFile: RandomAccessFile? = null
        var file: File? = null
        try {
            var downloadedLength: Long = 0
            val downloadUrl: String? = params[0]
            val fileName = "myJson"
            // Save file in local directory

            val directory = context.filesDir
            file = File(directory, fileName)
            if (file.exists()) {
                file.delete() // Clear available files
                downloadedLength = 0
            }
            val contentLength = downloadUrl?.run { getContentLength(this) }
            if (contentLength == 0L) return TYPE_FAILED
            val client = OkHttpClient()
            val request: Request =
                Request.Builder().addHeader("RANGE", "bytes = $downloadedLength-")
                    .url(downloadUrl!!)
                    .build()
            val response = client.newCall(request).execute()
            inputStream = Objects.requireNonNull(response.body)!!.byteStream()
            accessFile = RandomAccessFile(file, "rw")
            accessFile.seek(downloadedLength) // omit the downloaded bytes
            val b = ByteArray(1024)
            var total = 0
            var len: Int
            while (inputStream.read(b).also { len = it } != -1) {
                if (isCanceled) {
                    return TYPE_CANCELED
                } else if (isPaused) {
                    return TYPE_PAUSED
                } else {
                    total += len
                    accessFile.write(b, 0, len)
                    // calculate the percentages of downloaded part
                    val progress = ((total + downloadedLength) * 100 / contentLength!!).toInt()
                    publishProgress(progress)
                }
            }
            Objects.requireNonNull(response.body)!!.close()
            return TYPE_SUCCESS
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
                accessFile?.close()
                if (isCanceled && file != null) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return TYPE_FAILED
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        val progress = values[0]
        if (progress != null) {
            if (progress > lastProgress) {
                downloadListener.onProgress(progress)
                lastProgress = progress
            }
        }
    }

    override fun onPostExecute(status: Int) {
        when (status) {
            TYPE_SUCCESS -> downloadListener.onSuccess()
            TYPE_FAILED -> downloadListener.onFailed()
            TYPE_PAUSED -> downloadListener.onPaused()
            TYPE_CANCELED -> downloadListener.onCanceled()
            else -> {
            }
        }
    }

    private fun getContentLength(downloadUrl: String): Long {
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(downloadUrl)
            .build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val contentLength = Objects.requireNonNull(response.body)!!.contentLength()
            response.close()
            return contentLength
        }
        return 0
    }


}