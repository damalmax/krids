package com.thehadalone.krids.hex

import com.thehadalone.krids.core.CellStorage
import com.thehadalone.krids.core.Grid
import com.thehadalone.krids.core.Point
import java.lang.Math.*

/**
 * @author Roman Zakharenko
 */
class HexGrid<T> internal constructor(val hexOrientation: HexOrientation,
                                      val origin: Point,
                                      val hexWidth: Double,
                                      val hexHeight: Double,
                                      override val cells: CellStorage<Hex<T>>) : Grid<Hex<T>> {

    override fun getCellByScreenPosition(x: Double, y: Double): Hex<T>? {
        val normX = (x - origin.x) / hexWidth
        val normY = (y - origin.y) / hexHeight

        val coordX = round(hexOrientation.backward[0] * normX + hexOrientation.backward[1] * normY)
        val coordY = round(hexOrientation.backward[2] * normX + hexOrientation.backward[3] * normY)

        return cells[coordX.toInt(), coordY.toInt()]
    }
}

class HexBuilder<T> internal constructor(val orientation: HexOrientation,
                                         val hexWidth: Double,
                                         val hexHeight: Double,
                                         val gridOrigin: Point) {

    internal fun createHexWithCoordinates(x: Int, y: Int): Hex<T> {
        val center = getHexCenterScreenPositionByCoordinates(x, y)
        val corners = getHexCornersByCenter(center)

        return Hex(x, y, center, corners)
    }

    private fun getHexCenterScreenPositionByCoordinates(x: Int, y: Int): Point {
        val posX = hexWidth * (orientation.forward[0] * x + orientation.forward[1] * y)
        val posY = hexHeight * (orientation.forward[2] * x + orientation.forward[3] * y)

        return Point(posX + gridOrigin.x, posY + gridOrigin.y)
    }

    private fun getHexCornersByCenter(center: Point): Collection<Point> {
        return (0..5).map {
            val angle = 2 * PI * (orientation.angle + it) / 6
            val x = center.x + hexWidth + cos(angle)
            val y = center.y + hexHeight + sin(angle)

            Point(x, y)
        }
    }
}

class ParallelogramGridBuilder<T> internal constructor(val hexOrientation: HexOrientation,
                                                       val origin: Point,
                                                       val hexWidth: Double,
                                                       val hexHeight: Double,
                                                       val gridWidth: Int,
                                                       val gridHeight: Int) {

    private val hexBuilder = HexBuilder<T>(hexOrientation, hexWidth, hexHeight, origin)

    fun build(): HexGrid<T> {
        val hexStorage = CellStorage<Hex<T>>()
        for (x in 0..gridWidth) {
            for (y in 0..gridHeight) {
                hexStorage[x, y] = hexBuilder.createHexWithCoordinates(x, y)
            }
        }

        return HexGrid(hexOrientation, origin, hexWidth, hexHeight, hexStorage)
    }
}

class TriangleGridBuilder<T> internal constructor(val hexOrientation: HexOrientation,
                                                  val origin: Point,
                                                  val hexWidth: Double,
                                                  val hexHeight: Double,
                                                  val gridSideSize: Int) {

    private val hexBuilder = HexBuilder<T>(hexOrientation, hexWidth, hexHeight, origin)

    fun build(): HexGrid<T> {
        val hexStorage = CellStorage<Hex<T>>()
        for (x in 0..gridSideSize) {
            for (y in 0..gridSideSize - x) {
                hexStorage[x, y] = hexBuilder.createHexWithCoordinates(x, y)
            }
        }

        return HexGrid(hexOrientation, origin, hexWidth, hexHeight, hexStorage)
    }
}

class HexagonGridBuilder<T> internal constructor(val hexOrientation: HexOrientation,
                                                 val origin: Point,
                                                 val hexWidth: Double,
                                                 val hexHeight: Double,
                                                 val gridRadius: Int) {

    private val hexBuilder = HexBuilder<T>(hexOrientation, hexWidth, hexHeight, origin)

    fun build(): HexGrid<T> {
        val hexStorage = CellStorage<Hex<T>>()
        for (x in -gridRadius..gridRadius) {
            val r1 = max(-gridRadius, -x - gridRadius)
            val r2 = min(gridRadius, -x + gridRadius)

            for (y in r1..r2) {
                hexStorage[x, y] = hexBuilder.createHexWithCoordinates(x, y)
            }
        }

        return HexGrid(hexOrientation, origin, hexWidth, hexHeight, hexStorage)
    }
}

class RectangleGridBuilder<T> internal constructor(val hexOrientation: HexOrientation,
                                                   val origin: Point,
                                                   val hexWidth: Double,
                                                   val hexHeight: Double,
                                                   val gridWidth: Int,
                                                   val gridHeight: Int) {

    private val hexBuilder = HexBuilder<T>(hexOrientation, hexWidth, hexHeight, origin)

    fun build(): HexGrid<T> {
        val hexStorage = CellStorage<Hex<T>>()
        for (x in 0..gridHeight) {
            val xOffset = x / 2
            for (y in -xOffset..gridWidth - xOffset) {
                hexStorage[x, y] = hexBuilder.createHexWithCoordinates(x, y)
            }
        }

        return HexGrid(hexOrientation, origin, hexWidth, hexHeight, hexStorage)
    }
}
