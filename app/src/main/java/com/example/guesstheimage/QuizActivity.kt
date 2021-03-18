package com.example.guesstheimage

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.ArrayList

class QuizActivity : AppCompatActivity() {
    // Declare variables
    private var quizList: List<Quiz>? = null
    private var seconds = 0
    private var indexCurrentQuestion = 0
    private var questionView: TextView? = null
    private var imageView: ImageView? = null
    private var radioGroup: RadioGroup? = null
    private var radioButtonOne: RadioButton? = null
    private var radioButtonTwo: RadioButton? = null
    private var radioButtonThree: RadioButton? = null
    private var radioButtonFour: RadioButton? = null
    private var buttonPrevious: Button? = null
    private var buttonNext: Button? = null
    private var textTime: TextView? = null
    private var countDownTimer: CountDownTimer? = null
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // Hide toolbar
        Objects.requireNonNull(supportActionBar)!!.hide()

        // Define Activity views
        questionView = findViewById(R.id.celebrityQuestion)
        imageView = findViewById(R.id.celebrityImage)
        radioGroup = findViewById(R.id.celebrityOption)
        radioButtonOne = findViewById(R.id.radioButtonOne)
        radioButtonTwo = findViewById(R.id.radioButtonTwo)
        radioButtonThree = findViewById(R.id.radioButtonThree)
        radioButtonFour = findViewById(R.id.radioButtonFour)
        textTime = findViewById(R.id.textTime)

        // setOnClickListener and set checked onClick for each button
        radioButtonOne?.setOnClickListener(View.OnClickListener { view ->
            (view as RadioButton).isChecked = true
            quizList!![indexCurrentQuestion].userAnswer = 1
        })
        radioButtonTwo?.setOnClickListener(View.OnClickListener { view ->
            (view as RadioButton).isChecked = true
            quizList!![indexCurrentQuestion].userAnswer = 2
        })
        radioButtonThree?.setOnClickListener(View.OnClickListener { view ->
            (view as RadioButton).isChecked = true
            quizList!![indexCurrentQuestion].userAnswer = 3
        })
        radioButtonFour?.setOnClickListener(View.OnClickListener { view ->
            (view as RadioButton).isChecked = true
            quizList!![indexCurrentQuestion].userAnswer = 4
        })

        // Define button views
        buttonNext = findViewById(R.id.buttonNext)
        buttonPrevious = findViewById(R.id.buttonPrevious)

        // Access intent interface and get variables
        val intent = intent
        val level = intent.getIntExtra("level", 0)
        seconds = intent.getIntExtra("seconds", 30)
        var string: String? = null

        // Safely read data from saved file
        try {
            val fileInputStream = openFileInput("myJson")
            val inputStreamReader = InputStreamReader(fileInputStream, StandardCharsets.UTF_8)
            val bufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            string = stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val gson = Gson()
        val type = object : TypeToken<List<Quiz?>?>() {}.type
        val list = gson.fromJson<List<Quiz>>(string, type)

        // Set sublist based on user set level
        quizList = if (level == 1) {
            assert(list != null)
            list!!.subList(0, 5)
        } else if (level == 2) {
            assert(list != null)
            list!!.subList(5, 10)
        } else {
            assert(list != null)
            list!!.subList(10, 15)
        }

        // initialise and set for each index in current activity as current question
        indexCurrentQuestion = 0
        val currentQuestion = quizList!![indexCurrentQuestion]
        currentQuestionView(currentQuestion)
        buttonPrevious?.isEnabled = false // Disable previous button when current index is 0

        // See function
        startTimer()

        // When user submit quiz, stop time and start Solution Activity
        val buttonSubmit = findViewById<Button>(R.id.buttonSubmit)
        buttonSubmit.setOnClickListener {
            stopTimer()
            val i = Intent(this@QuizActivity, SolutionActivity::class.java)
            i.putExtra("score", score)
            // Change List to ArrayList to accommodate subList
            val list = ArrayList(quizList!!)
            i.putExtra("quizList", list)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(i)
        }
    }

    // Start countdown. OnFinish, start Solution Activity
    fun startTimer() {
        textTime!!.text = seconds.toString()
        countDownTimer = object : CountDownTimer((seconds * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                textTime?.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                val i = Intent(this@QuizActivity, SolutionActivity::class.java)
                i.putExtra("score", score)
                // Change List to ArrayList to accommodate subList
                val list = ArrayList(quizList!!)
                i.putExtra("quizList", list)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(i)
            }
        }.start()
    }

    // Cancel timer to prevent countDown in background
    // If not defined, Solution Activity will start even when user goes back to
    // Main Activity because Quiz Activity doesn't get destroyed instantly
    fun stopTimer() {
        countDownTimer!!.cancel()
    }

    // Pre-define new views before setting previous question as current question, for index < 0
    fun onButtonPrevious(view: View?) {
        if (indexCurrentQuestion != 0) {
            indexCurrentQuestion--
            if (indexCurrentQuestion == 0) buttonPrevious!!.isEnabled = false
            if (indexCurrentQuestion != quizList!!.size - 1) buttonNext!!.isEnabled = true
            val currentQuestion = quizList!![indexCurrentQuestion]
            currentQuestionView(currentQuestion)
            radioGroup = findViewById(R.id.celebrityOption)
            if (currentQuestion.userAnswer == 0) radioGroup?.clearCheck() else {
                when (currentQuestion.userAnswer) {
                    1 -> {
                        radioGroup?.check(R.id.radioButtonOne)
                    }
                    2 -> {
                        radioGroup?.check(R.id.radioButtonTwo)
                    }
                    3 -> {
                        radioGroup?.check(R.id.radioButtonThree)
                    }
                    4 -> {
                        radioGroup?.check(R.id.radioButtonFour)
                    }
                }
            }
        }
    }

    // Pre-define new views before setting next question as current question, for index > list.size()
    fun onButtonNext(view: View?) {
        if (indexCurrentQuestion != quizList!!.size - 1) {
            indexCurrentQuestion++
            if (indexCurrentQuestion == quizList!!.size - 1) buttonNext!!.isEnabled = false
            if (indexCurrentQuestion != 0) buttonPrevious!!.isEnabled = true
            val currentQuestion = quizList!![indexCurrentQuestion]
            currentQuestionView(currentQuestion)
            radioGroup = findViewById(R.id.celebrityOption)
            if (currentQuestion.userAnswer == 0) radioGroup?.clearCheck() else {
                when (currentQuestion.userAnswer) {
                    1 -> {
                        radioGroup?.check(R.id.radioButtonOne)
                    }
                    2 -> {
                        radioGroup?.check(R.id.radioButtonTwo)
                    }
                    3 -> {
                        radioGroup?.check(R.id.radioButtonThree)
                    }
                    4 -> {
                        radioGroup?.check(R.id.radioButtonFour)
                    }
                }
            }
        }
    }

    fun currentQuestionView(currentQuestion: Quiz) {
        questionView!!.text = String.format("%s. %s", indexCurrentQuestion + 1, currentQuestion.question)
        radioButtonOne!!.text = currentQuestion.one
        radioButtonTwo!!.text = currentQuestion.two
        radioButtonThree!!.text = currentQuestion.three
        radioButtonFour!!.text = currentQuestion.four
        Glide.with(imageView!!.context).load(currentQuestion.imageUrl).into(imageView!!)
    }

    // Calculate score
    val score: Int
        get() {
            var score = 0
            for (i in quizList!!.indices) {
                if (quizList!![i].userAnswer == quizList!![i].correctAnswer) score++
            }
            return score
        }
}
