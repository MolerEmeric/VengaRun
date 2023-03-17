package com.venga.vengarubik.models

data class Breakable(override var imgId: Int, override var pos: Float, var nbClick: Int = 0) : Obstacle() {
}