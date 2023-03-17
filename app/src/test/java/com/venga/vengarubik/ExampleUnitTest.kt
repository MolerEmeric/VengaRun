package com.venga.vengarubik

import com.venga.vengarubik.models.ExtremeCube
import com.venga.vengarubik.models.HardCube
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {

    var vengaCube = ExtremeCube()

    @Before
    fun setup() {

    }

    @Test
    fun test() {
        vengaCube.actualCube = mutableListOf(
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
            mutableListOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2),
            mutableListOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
            mutableListOf(4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4),
            mutableListOf(5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5)
        )

        println(vengaCube.actualCube)
        vengaCube.flipFront(0, Direction.DOWN)
        vengaCube.flipFront(1, Direction.DOWN)
        vengaCube.flipFront(2, Direction.DOWN)
        vengaCube.flipFront(3, Direction.DOWN)
        println(vengaCube.actualCube)
    }
}