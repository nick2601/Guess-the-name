package com.example.guesstheimage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class SolutionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solution)

        // Define Navigation
        Objects.requireNonNull(supportActionBar)!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        if (supportActionBar != null) {
            supportActionBar!!.title = "Results"
        }

        // Interface instance to get values from QuizActivity
        val scoreValue = intent.getIntExtra("score", 0)
        val quizList = intent.getSerializableExtra("quizList") as List<Quiz>

        // Set view and display scoreValue
        val scoreView = findViewById<TextView>(R.id.scoreTextView)
        scoreView.text = scoreValue.toString()

        // Set score out-of view
        val scoreTotalView = findViewById<TextView>(R.id.scoreTotalTextView)
        scoreTotalView.text = 5.toString()

        // See function
        displayWellDone(scoreValue)

        // RecycleView definitions
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val solutionAdapter = SolutionAdapter(quizList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = solutionAdapter
    }

    // Function to display well done image if user gets all correct | also settings for total value
    fun displayWellDone(score: Int) {

        // Set view for well done image
        val imageView = findViewById<ImageView>(R.id.wellDoneImage)
        imageView.visibility = View.INVISIBLE // set image invisible

        // display well done image if user gets all correct
        if (score == 5) imageView.visibility = View.VISIBLE
    }
}