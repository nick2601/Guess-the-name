package com.example.guesstheimage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private var radioButtonLevelOne: RadioButton? = null
    private var radioButtonLevelTwo: RadioButton? = null
    private var radioButtonLevelThree: RadioButton? = null
    private var radioButton30: RadioButton? = null
    private var radioButton60: RadioButton? = null
    private var radioButton90: RadioButton? = null
    private var progressBarDownload: ProgressBar? = null
    private var buttonStartQuiz: Button? = null
    var level = 0
    var seconds = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Define Level views
        radioButtonLevelOne = findViewById(R.id.radio)
        radioButtonLevelTwo = findViewById(R.id.radioButtonLevelTwo)
        radioButtonLevelThree = findViewById(R.id.radioButtonLevelThree)
        radioButtonLevelOne?.isChecked = true
        radioButtonLevelTwo?.isChecked = false
        radioButtonLevelThree?.isChecked = false

        // Define Time views
        radioButton30 = findViewById(R.id.radioButton30)
        radioButton60 = findViewById(R.id.radioButton60)
        radioButton90 = findViewById(R.id.radioButton90)
        radioButton30?.isChecked = true
        radioButton60?.isChecked = false
        radioButton90?.isChecked = false

        // Define Download views
        progressBarDownload = findViewById(R.id.progressBarDownload)
        progressBarDownload?.max = 100

        // Define Update and Starting buttons
        val buttonUpdate = findViewById<Button>(R.id.buttonUpdate)
        buttonStartQuiz = findViewById(R.id.buttonStartQuiz)
        buttonUpdate.isEnabled = true
        buttonStartQuiz?.isEnabled = false
        downloadTask = null // Always initialize task to null
    }

    private var downloadTask: DownloadTask? = null

    // Define Download methods
    private val downloadListener: DownloadListener = object : DownloadListener {
        override fun onProgress(progress: Int) {
            progressBarDownload!!.progress = progress
        }

        override fun onSuccess() {
            downloadTask = null
            progressBarDownload!!.progress = progressBarDownload!!.max
            buttonStartQuiz!!.isEnabled = true // Enable Start button when download is successful
        }

        override fun onFailed() {
            downloadTask = null
            //when download failed, close the foreground notification and create a new one about the failure
            Toast.makeText(applicationContext, "Download Failed", Toast.LENGTH_SHORT).show()
        }

        override fun onPaused() {
            downloadTask = null
            Toast.makeText(applicationContext, "Paused", Toast.LENGTH_SHORT).show()
        }

        override fun onCanceled() {
            downloadTask = null
            Toast.makeText(applicationContext, "Canceled", Toast.LENGTH_SHORT).show()
        }
    }

    fun onButtonUpdate(view: View?) {
        if (downloadTask == null) {
            // Import data from internet
            val jsonUrl = "https://api.jsonbin.io/b/5e8f60bb172eb6438960f731"
            downloadTask = DownloadTask(downloadListener, this)
            downloadTask!!.execute(jsonUrl)
        }
    }

    // Start QuizActivity with user settings/choices
    fun onButtonStartQuiz() {
        if (radioButtonLevelOne!!.isChecked) level = 1
        if (radioButtonLevelTwo!!.isChecked) level = 2
        if (radioButtonLevelThree!!.isChecked) level = 3
        if (radioButton30!!.isChecked) seconds = 30
        if (radioButton60!!.isChecked) seconds = 60
        if (radioButton90!!.isChecked) seconds = 90
        val intent = Intent(this, QuizActivity::class.java)
        intent.putExtra("level", level)
        intent.putExtra("seconds", seconds)
        startActivity(intent)
    }
}