package com.example.comp3717_project.database

data class User(private val name: String, private val password: String, private var highScore: Int = 0) {

    fun getName(): String {
        return name
    }

    fun getHighScore(): Int {
        return highScore
    }

    fun setHighScore(newHighScore: Int) {
        highScore = newHighScore
    }

}