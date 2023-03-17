package com.venga.vengarubik.models

class User (val pseudo: String, val difficulty: Int, val score: Long) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "pseudo" to pseudo,
            "scores" to mapOf( difficulty.toString() to arrayListOf(score))
        )
    }
}