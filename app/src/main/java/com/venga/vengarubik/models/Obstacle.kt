package com.venga.vengarubik.models

import android.widget.ImageView

abstract class Obstacle {
    abstract val img: ImageView
    abstract val pos: Float
}