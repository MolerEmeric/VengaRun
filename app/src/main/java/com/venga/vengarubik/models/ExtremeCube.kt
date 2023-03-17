package com.venga.vengarubik.models

import com.venga.vengarubik.Direction
import com.venga.vengarubik.Face

/*** 4x4 Venga Rubik's Cube ***/
class ExtremeCube : Cube {
    override lateinit var initialCube: MutableList<MutableList<Int>>
    override var actualCube = mutableListOf(
        mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        mutableListOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2),
        mutableListOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
        mutableListOf(4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4),
        mutableListOf(5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5)
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
        shuffle(50)
        initialCube = deepCopyCube(actualCube)
    }

    override fun solve() {
        actualCube = mutableListOf(
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
            mutableListOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2),
            mutableListOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
            mutableListOf(4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4),
            mutableListOf(5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5)
        )
    }

    override fun reset() {
        actualCube = deepCopyCube(initialCube)
    }

    override fun flip(face: Face, case: Int, dir: Direction) {
        when (face) {
            Face.FRONT -> flipFront(case, dir)
            Face.BACK -> {
                for (i in 0..3) {
                    flipFront(i, Direction.LEFT)
                    flipFront(i, Direction.LEFT)
                }
                flipFront(case, dir)
                for (i in 0..3) {
                    flipFront(i, Direction.RIGHT)
                    flipFront(i, Direction.RIGHT)
                }
            }
            Face.TOP -> {
                for (i in 0..3) flipFront(i, Direction.DOWN)
                flipFront(case, dir)
                for (i in 0..3) flipFront(i, Direction.UP)
            }
            Face.BOT -> {
                for (i in 0..3) flipFront(i, Direction.UP)
                flipFront(case, dir)
                for (i in 0..3) flipFront(i, Direction.DOWN)
            }
            Face.RIGHT -> {
                for (i in 0..12 step 4) flipFront(i, Direction.LEFT)
                flipFront(case, dir)
                for (i in 0..12 step 4) flipFront(i, Direction.RIGHT)
            }
            Face.LEFT -> {
                for (i in 0..12 step 4) flipFront(i, Direction.RIGHT)
                flipFront(case, dir)
                for (i in 0..12 step 4) flipFront(i, Direction.LEFT)
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
                actualCube[rotationFaces[i]][if (!dstIsBack) (case + 4) % 16 else getBackIndex((case + 4) % 16)] =
                    srcFace[if (!srcIsBack) (case + 4) % 16 else getBackIndex((case + 4) % 16)]
                actualCube[rotationFaces[i]][if (!dstIsBack) (case + 8) % 16 else getBackIndex((case + 8) % 16)] =
                    srcFace[if (!srcIsBack) (case + 8) % 16 else getBackIndex((case + 8) % 16)]
                actualCube[rotationFaces[i]][if (!dstIsBack) (case + 12) % 16 else getBackIndex((case + 12) % 16)] =
                    srcFace[if (!srcIsBack) (case + 12) % 16 else getBackIndex((case + 12) % 16)]
            }

            when (case) {
                0, 4, 8, 12 -> rotateFace(Face.LEFT, dir == Direction.DOWN) // col 1
                3, 7, 11, 15 -> rotateFace(Face.RIGHT, dir == Direction.UP)  // col 4
            }
        } else {
            rotationFaces =
                listOf(Face.FRONT.value, Face.RIGHT.value, Face.BACK.value, Face.LEFT.value)
            clockwise = dir == Direction.LEFT
            for (i in 0..3) {
                val srcFace =
                    cubeCopy[if (clockwise) rotationFaces[(i + 1) % 4] else rotationFaces[(i + 3) % 4]]
                actualCube[rotationFaces[i]][(case / 4) * 4] = srcFace[(case / 4) * 4]
                actualCube[rotationFaces[i]][(case / 4) * 4 + 1] = srcFace[(case / 4) * 4 + 1]
                actualCube[rotationFaces[i]][(case / 4) * 4 + 2] = srcFace[(case / 4) * 4 + 2]
                actualCube[rotationFaces[i]][(case / 4) * 4 + 3] = srcFace[(case / 4) * 4 + 3]
            }


            when (case) {
                0, 1, 2, 3 -> rotateFace(Face.TOP, dir == Direction.LEFT) // row 1
                12, 13, 14, 15 -> rotateFace(Face.BOT, dir == Direction.RIGHT) // row 4
            }
        }
    }

    private fun rotateFace(face: Face, clockwise: Boolean) {
        actualCube[face.value] = mutableListOf(
            actualCube[face.value][if (clockwise) 12 else 3], // 15 -->  12 / 3
            actualCube[face.value][if (clockwise) 8 else 7],  // 14 -->  8 / 7
            actualCube[face.value][if (clockwise) 4 else 11], // 13 -->  4 / 11
            actualCube[face.value][if (clockwise) 0 else 15], // 12 -->  0 / 15
            actualCube[face.value][if (clockwise) 13 else 2], // 11 -->  13 / 2
            actualCube[face.value][if (clockwise) 9 else 6],  // 10 -->  9 / 6
            actualCube[face.value][if (clockwise) 5 else 10], // 9  -->  5 / 10
            actualCube[face.value][if (clockwise) 1 else 14], // 8  -->  1 / 14
            actualCube[face.value][if (clockwise) 14 else 1], // 7  -->  14 / 1
            actualCube[face.value][if (clockwise) 10 else 5], // 6  -->  10 / 5
            actualCube[face.value][if (clockwise) 6 else 9],  // 5  -->  6 / 9
            actualCube[face.value][if (clockwise) 2 else 13], // 4  -->  2 / 13
            actualCube[face.value][if (clockwise) 15 else 0], // 3  -->  15 / 0
            actualCube[face.value][if (clockwise) 11 else 4], // 2  -->  11 / 4
            actualCube[face.value][if (clockwise) 7 else 8],  // 1  -->  7 / 8
            actualCube[face.value][if (clockwise) 3 else 12], // 0  -->  3 / 12
        )
    }

    private fun getBackIndex(i: Int): Int {
        return when (i) {
            0 -> 15
            1 -> 14
            2 -> 13
            3 -> 12
            4 -> 11
            5 -> 10
            6 -> 9
            7 -> 8
            8 -> 7
            9 -> 6
            10 -> 5
            11 -> 4
            12 -> 3
            13 -> 2
            14 -> 1
            else -> 0
        }
    }

    override fun shuffle(nbFlip: Int) {
        for (i in 0 until nbFlip) flipFront(
            listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15).random(),
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