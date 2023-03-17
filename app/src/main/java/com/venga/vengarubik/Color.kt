package com.venga.vengarubik

enum class Color(
    val value: Int,
    val red: Int,
    val green: Int,
    val blue: Int,
    val hue: Float,
    val saturation: Float,
    val brightness: Float
) {
    ORANGE(0, 255, 88, 0, 21f, 1f, 1f), // RGB #FF5800
    RED(1, 184, 0, 37, 348f, 1f, 0.72f), // RGB #B80025
    WHITE(2, 255, 255, 255, 0f, 0f, 1f), // RGB #FFFFFF
    BLUE(3, 0, 70, 173, 216f, 1f, 0.68f), // RGB #0046AD
    YELLOW(4, 255, 213, 0, 50f, 1f, 1f), // RGB #FFD500
    GREEN(5, 0, 155, 72, 148f, 1f, 0.61f); // RGB #009B48

    companion object {
        infix fun from(value: Int): Color = values().first { it.value == value }
    }
}