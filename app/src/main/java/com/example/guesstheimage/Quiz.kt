package com.example.guesstheimage

data class Quiz(var question: String, var imageUrl: String, var one: String, var two: String,
                var three: String, var four: String, var correctAnswer: Int, var userAnswer: Int)
