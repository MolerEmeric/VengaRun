package com.venga.vengarubik.models

class ScoreManager {

    @Volatile
    private var INSTANCE: ScoreManager? = null

    private val scores: MutableList<Int> = mutableListOf(3,1,2)
    private var pos =0

    fun getInstance(): ScoreManager? {
        if (INSTANCE == null) {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = ScoreManager()
                }
            }
        }
        return INSTANCE
    }

    fun getScore(): MutableList<Int> {
        return scores
    }

    fun addScore(score : Int) {
        if (scores.size < 5){
            scores[pos] = score
            pos ++
            scores.sort()
        }
        else{
            for (i in scores){
                if (score < scores[i]){
                    scores[i] = score
                    break
                }
            }
        }
    }

}
