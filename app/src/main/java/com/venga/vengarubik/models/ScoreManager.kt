package com.venga.vengarubik.models

object ScoreManager {
    var scores: ArrayList<Int> = ArrayList()

    fun addScore(score : Int) {
        scores.add(score)
        scores = ArrayList(scores.sortedDescending().take(5))
    }
}