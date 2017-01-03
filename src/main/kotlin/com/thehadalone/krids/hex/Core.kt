package com.thehadalone.krids.hex

import com.thehadalone.krids.core.Cell
import com.thehadalone.krids.core.Point
import java.lang.Math.sqrt

/**
 * @author Roman Zakharenko
 */
data class Hex<T>(override val x: Int,
                  override val y: Int,
                  override val center: Point,
                  override val corners: Collection<Point>,
                  override var data: T? = null) : Cell<T> {

    val z = -x - y
}

enum class HexOrientation(val forward: List<Double>, val backward: List<Double>, val angle: Double) {
    POINTY(listOf(sqrt(3.0), sqrt(3.0) / 2, 0.0, 3.0 / 2.0), listOf(sqrt(3.0) / 3, -1.0 / 3.0, 0.0, 2.0 / 3.0), 0.5),
    FLAT(listOf(3.0 / 2.0, 0.0, sqrt(3.0) / 2, sqrt(3.0)), listOf(2.0 / 3.0, 0.0, -1.0 / 3, sqrt(3.0) / 3), 0.0)
}

enum class HexGridShape {
    PARALLELOGRAM, TRIANGLE, HEXAGON, RECTANGLE
}
