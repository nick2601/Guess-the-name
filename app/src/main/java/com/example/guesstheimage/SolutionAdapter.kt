package com.example.guesstheimage

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SolutionAdapter(private val quizList: List<Quiz>, private val context: Context) : RecyclerView.Adapter<SolutionAdapter.ViewHolder>() {
    // Build view layout and call ViewHolder, QuizHolder class

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Define recycler views
        val viewQuestion = itemView.findViewById<TextView>(R.id.celebrityQuestion)
        val imageView = itemView.findViewById<ImageView>(R.id.celebrityImage)
        val radioGroup = itemView.findViewById<RadioGroup>(R.id.celebrityOption)
        val radioButtonOne = itemView.findViewById<RadioButton>(R.id.radioButtonOne)
        val radioButtonTwo = itemView.findViewById<RadioButton>(R.id.radioButtonTwo)
        val radioButtonThree = itemView.findViewById<RadioButton>(R.id.radioButtonThree)
        val radioButtonFour = itemView.findViewById<RadioButton>(R.id.radioButtonFour)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context).inflate(R.layout.solution, viewGroup, false)
      return ViewHolder(layoutInflater)
    }



    // Default ViewHolder methods
    override fun getItemCount(): Int {
        return quizList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.findViewById<View>(R.id.horizontalDivider)


        // Format recycler view content
        if (quizList?.isNotEmpty()) {
            val quiz = quizList[position!!]

            holder. viewQuestion.text = String.format("%s. %s", position + 1, quiz.question)
            Glide.with(holder.imageView.context).load(quiz.imageUrl).into(holder.imageView)
            holder.radioButtonOne.text = quiz.one
            holder.radioButtonTwo.text = quiz.two
            holder.radioButtonThree.text = quiz.three
            holder.radioButtonFour.text = quiz.four

            // This is crucial for Marking system
            /* First, determine if userAnswer is the same as correctAnswer, IF YES, mark it
            * green and set it checked. ELSE, if user didn't select anything clearCheck() else if
            * userAnswer is wrong, mark userAnswer red, locate
            * correctAnswer and mark it green.
            */
            if (quiz.userAnswer == quiz.correctAnswer) {
                when (quiz.correctAnswer) {
                    1 -> {
                        holder.radioButtonOne.isChecked = true
                        holder.radioButtonOne.setTextColor(Color.parseColor("#FF0BA512"))
                    }
                    2 -> {
                        holder.radioButtonTwo.isChecked = true
                        holder.radioButtonTwo.setTextColor(Color.parseColor("#FF0BA512"))
                    }
                    3 -> {
                        holder.radioButtonThree.isChecked = true
                        holder.radioButtonThree.setTextColor(Color.parseColor("#FF0BA512"))
                    }
                    4 -> {
                        holder.radioButtonFour.isChecked = true
                        holder.radioButtonFour.setTextColor(Color.parseColor("#FF0BA512"))
                    }
                }
            } else {
                if (1 == quiz.userAnswer) {
                    holder.radioButtonOne.isChecked = true
                    holder.radioButtonOne.setTextColor(Color.RED)
                }
                if (1 == quiz.correctAnswer) {
                    holder.radioButtonOne.setTextColor(Color.parseColor("#FF0BA512"))
                }
                if (2 == quiz.userAnswer) {
                    holder.radioButtonTwo.isChecked = true
                    holder.radioButtonTwo.setTextColor(Color.RED)
                }
                if (2 == quiz.correctAnswer) {
                    holder.radioButtonTwo.setTextColor(Color.parseColor("#FF0BA512"))
                }
                if (3 == quiz.userAnswer) {
                    holder. radioButtonThree.isChecked = true
                    holder.radioButtonThree.setTextColor(Color.RED)
                }
                if (3 == quiz.correctAnswer) {
                    holder.radioButtonThree.setTextColor(Color.parseColor("#FF0BA512"))
                }
                if (4 == quiz.userAnswer) {
                    holder.radioButtonFour.isChecked = true
                    holder.radioButtonFour.setTextColor(Color.RED)
                }
                if (4 == quiz.correctAnswer) {
                    holder.radioButtonFour.setTextColor(Color.parseColor("#FF0BA512"))
                }
            }
            if (0 == quiz.userAnswer) holder.radioGroup.clearCheck()

            // Disable all radioButton to avoid answer misinterpretations
            holder.radioButtonOne.isEnabled = false
            holder.radioButtonTwo.isEnabled = false
            holder. radioButtonThree.isEnabled = false
            holder.radioButtonFour.isEnabled = false
        }
    }


}