package com.venga.vengarubik.models

import com.venga.vengarubik.Direction
import com.venga.vengarubik.Face

/*** 2x2 Venga Rubik's Cube ***/
class NormalCube : Cube {
    override lateinit var initialCube: MutableList<MutableList<Int>>
    override var actualCube = mutableListOf(
        mutableListOf(0, 0, 0, 0), mutableListOf(1, 1, 1, 1), mutableListOf(2, 2, 2, 2),
        mutableListOf(3, 3, 3, 3), mutableListOf(4, 4, 4, 4), mutableListOf(5, 5, 5, 5)
    )

    override var isSolved = false
        get() {
            for (face in actualCube) {
                for (case in face) {
                    if (case != face.first()) return false
                }
            }
            return true
        }

    override var isReset = true
        get() {
            for ((i, face) in actualCube.withIndex()) {
                for ((j, case) in face.withIndex()) {
                    if (case != initialCube[i][j]) return false
                }
            }
            return true
        }

    init {
        shuffle(20)
        initialCube = deepCopyCube(actualCube)
    }

    override fun solve() {
        actualCube = mutableListOf(
            mutableListOf(0, 0, 0, 0), mutableListOf(1, 1, 1, 1), mutableListOf(2, 2, 2, 2),
            mutableListOf(3, 3, 3, 3), mutableListOf(4, 4, 4, 4), mutableListOf(5, 5, 5, 5)
        )
    }

    override fun reset() {
        actualCube = deepCopyCube(initialCube)
    }

    override fun flip(face: Face, case: Int, dir: Direction) {
        when (face) {
            Face.FRONT -> flipFront(case, dir)
            Face.BACK -> {
                for (i in 0..1) {
                    flipFront(i, Direction.LEFT)
                    flipFront(i, Direction.LEFT)
                }
                flipFront(case, dir)
                for (i in 0..1) {
                    flipFront(i, Direction.RIGHT)
                    flipFront(i, Direction.RIGHT)
                }
            }
            Face.TOP -> {
                for (i in 0..1) flipFront(i, Direction.DOWN)
                flipFront(case, dir)
                for (i in 0..1) flipFront(i, Direction.UP)
            }
            Face.BOT -> {
                for (i in 0..1) flipFront(i, Direction.UP)
                flipFront(case, dir)
                for (i in 0..1) flipFront(i, Direction.DOWN)
            }
            Face.RIGHT -> {
                for (i in 0..2 step 2) flipFront(i, Direction.LEFT)
                flipFront(case, dir)
                for (i in 0..2 step 2) flipFront(i, Direction.RIGHT)
            }
            Face.LEFT -> {
                for (i in 0..2 step 2) flipFront(i, Direction.RIGHT)
                flipFront(case, dir)
                for (i in 0..2 step 2) flipFront(i, Direction.LEFT)
            }
        }
    }

    fun flipFront(case: Int, dir: Direction) {
        val cubeCopy: MutableList<MutableList<Int>> = deepCopyCube(actualCube)
        var clockwise = dir == Direction.DOWN
        var rotationFaces =
            listOf(Face.FRONT.value, Face.TOP.value, Face.BACK.value, Face.BOT.value)

        if (dir == Direction.UP || dir == Direction.DOWN) {
            for (i in 0..3) {
                val srcIsBack =
                    (Face from (if (clockwise) rotationFaces[(i + 1) % 4] else rotationFaces[(i + 3) % 4])) == Face.BACK
                val dstIsBack = (Face from rotationFaces[i]) == Face.BACK
                val srcFace =
                    cubeCopy[if (clockwise) rotationFaces[(i + 1) % 4] else rotationFaces[(i + 3) % 4]]

                actualCube[rotationFaces[i]][if (!dstIsBack) case else getBackIndex(case)] =
                    srcFace[if (!srcIsBack) case else getBackIndex(case)]
                actualCube[rotationFaces[i]][if (!dstIsBack) (case + 2) % 4 else getBackIndex((case + 2) % 4)] =
                    srcFace[if (!srcIsBack) (case + 2) % 4 else getBackIndex((case + 2) % 4)]
            }

            when (case) {
                0, 2 -> rotateFace(Face.LEFT, dir == Direction.DOWN) // col 1
                1, 3 -> rotateFace(Face.RIGHT, dir == Direction.UP)  // col 2
            }
        } else {
            rotationFaces =
                listOf(Face.FRONT.value, Face.RIGHT.value, Face.BACK.value, Face.LEFT.value)
            clockwise = dir == Direction.LEFT
            for (i in 0..3) {
                val srcFace =
                    cubeCopy[if (clockwise) rotationFaces[(i + 1) % 4] else rotationFaces[(i + 3) % 4]]

                actualCube[rotationFaces[i]][(case / 2) * 2] = srcFace[(case / 2) * 2]
                actualCube[rotationFaces[i]][(case / 2) * 2 + 1] = srcFace[(case / 2) * 2 + 1]
            }

            when (case) {
                0, 1 -> rotateFace(Face.TOP, dir == Direction.LEFT) // row 1
                2, 3 -> rotateFace(Face.BOT, dir == Direction.RIGHT) // row 2
            }
        }
    }

    private fun rotateFace(face: Face, clockwise: Boolean) {
        actualCube[face.value] = mutableListOf(
            actualCube[face.value][if (clockwise) 2 else 1], // 0 --> 2 / 1
            actualCube[face.value][if (clockwise) 0 else 3], // 1 --> 0 / 3
            actualCube[face.value][if (clockwise) 3 else 0], // 2 --> 3 / 0
            actualCube[face.value][if (clockwise) 1 else 2]  // 3 --> 1 / 2
        )
    }

    private fun getBackIndex(i: Int): Int {
        return when (i) {
            0 -> 3
            1 -> 2
            2 -> 1
            else -> 0
        }
    }

    override fun shuffle(nbFlip: Int) {
        for (i in 0 until nbFlip) flipFront(
            listOf(0, 1, 2, 3).random(),
            Direction.values().random()
        )
    }

    private fun deepCopyCube(cube: MutableList<MutableList<Int>>): MutableList<MutableList<Int>> {
        return mutableListOf(
            cube[0].toMutableList(), cube[1].toMutableList(), cube[2].toMutableList(),
            cube[3].toMutableList(), cube[4].toMutableList(), cube[5].toMutableList()
        )
    }

}