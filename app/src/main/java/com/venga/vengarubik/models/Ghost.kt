package com.venga.vengarubik.models

import android.widget.ImageView

data class Ghost(override val img: ImageView, override val pos: Float) : Obstacle() {
}