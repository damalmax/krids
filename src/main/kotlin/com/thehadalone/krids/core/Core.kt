package com.thehadalone.krids.core

/**
 * @author Roman Zakharenko
 */
data class Point(val x: Double, val y: Double)

interface Cell<T> {
    val x: Int
    val y: Int
    val center: Point
    val corners: Collection<Point>
    var data: T?
}

interface Grid<T : Cell<*>> {
    val cells: CellStorage<T>

    fun getCellByScreenPosition(x: Double, y: Double): T?
}

class CellStorage<T : Cell<*>> internal constructor() : Iterable<T> {
    private val cells = mutableMapOf<Int, MutableMap<Int, T>>()

    operator fun get(x: Int, y: Int): T? = cells[x]?.get(y)

    internal operator fun set(x: Int, y: Int, value: T) = cells
            .getOrPut(x, { mutableMapOf() })
            .set(y, value)

    override operator fun iterator(): Iterator<T> = CellIterator()

    inner class CellIterator : AbstractIterator<T>() {
        private val columnIterator = cells.iterator()
        private var itemIterator = if (columnIterator.hasNext()) {
            columnIterator.next().value.iterator()
        } else {
            emptyMap<Int, T>().iterator()
        }

        override fun computeNext() {
            if (itemIterator.hasNext()) {
                setNext(itemIterator.next().value)
            } else if (columnIterator.hasNext()) {
                itemIterator = columnIterator.next().value.iterator()
                computeNext()
            } else {
                done()
            }
        }
    }
}
