package com.venga.vengarubik.models

import com.venga.vengarubik.Direction
import com.venga.vengarubik.Face

interface Cube {
    var initialCube: MutableList<MutableList<Int>>
    var actualCube: MutableList<MutableList<Int>>
    var isSolved: Boolean

    fun solve()

    fun reset()

    fun flip(face: Face, case: Int, dir: Direction)

    fun shuffle(nbFlip: Int)
    var isReset: Boolean
}