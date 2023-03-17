package com.venga.vengarubik

enum class Face(val value: Int) {
    TOP(0), BOT(1), FRONT(2), RIGHT(3), BACK(4), LEFT(5);

    companion object {
        infix fun from(value: Int): Face = Face.values().first { it.value == value }
    }
}