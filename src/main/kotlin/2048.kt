package org.example

import java.util.Scanner
import kotlin.math.max

object `2048` {
    enum class Direction {
        UP, LEFT, DOWN, RIGHT
    }
    class Grid {
        val data: Array<Array<Int>>

        constructor(data: Array<Int>) {
            this.data = arrayOf(
                data.copyOfRange(0, 4),
                data.copyOfRange(4, 8),
                data.copyOfRange(8, 12),
                data.copyOfRange(12, 16)
            )
        }
        constructor(data: List<Int>) : this(data.toTypedArray())
        constructor(data: Array<Array<Int>>) {
            this.data = Array<Array<Int>>(4) {
                data[it].copyOf()
            }
        }
        constructor(grid: Grid) : this(grid.data)

        fun get(posX: Int, posY: Int): Int = data[posX][posY]
        fun set(posX: Int, posY: Int, value: Int) { data[posX][posY] = value }

        fun getSideOf(direction: Direction, posX: Int, posY: Int): Int? {
            var posx = posX
            var posy = posY
            when{
                direction == Direction.UP && posX == 0 -> return null
                direction == Direction.DOWN && posX == 3 -> return null
                direction == Direction.LEFT && posY == 0 -> return null
                direction == Direction.RIGHT && posY == 3 -> return null
            }
            when (direction) {
                Direction.UP -> posx--
                Direction.DOWN -> posx++
                Direction.LEFT -> posy--
                Direction.RIGHT -> posy++
            }
            return get(posx, posy)
        }

        fun setSideOf(direction: Direction, posX: Int, posY: Int, value: Int) {
            var posx = posX
            var posy = posY
            when {
                direction == Direction.UP && posx != 0 -> posx--
                direction == Direction.DOWN && posx != 3 -> posx++
                direction == Direction.LEFT && posy != 0 -> posy--
                direction == Direction.RIGHT && posy != 3 -> posy++
            }
            set(posx, posy, value)
        }

        fun moveOne(direction: Direction, posX: Int, posY: Int) {
            if (get(posX, posY) == getSideOf(direction, posX, posY)) {
                setSideOf(direction, posX, posY, get(posX, posY) + getSideOf(direction, posX, posY)!!)
                set(posX, posY, 0)
            }
            if (getSideOf(direction, posX, posY) == 0) {
                setSideOf(direction, posX, posY, get(posX, posY))
                set(posX, posY, 0)
            }
        }

        var last: Grid? = null

        fun moveAll(direction: Direction) {
            last = null
            val rangeI = if (direction == Direction.DOWN) 3 downTo 0 else 0 until 4
            val rangeJ = if (direction == Direction.RIGHT) 3 downTo 0 else 0 until 4
            while (this!=last){
                last = Grid(this)
                for (i in rangeI) {
                    for (j in rangeJ) {
                        moveOne(direction, i, j)
                    }
                }
            }
        }

        override fun toString(): String {
            var a = StringBuilder(80)
            for (i in 0 until 4) {
                for (j in 0 until 4) {
                    a.append(data[i][j]).append(' ')
                }
                a.append('\n')
            }
            return a.toString()
        }

        override fun equals(other: Any?): Boolean {
            return if (other is Grid) {
                this.hashCode() == other.hashCode()
            } else {
                false
            }
        }

        override fun hashCode(): Int {
            return data.contentDeepHashCode()
        }


        fun evaluatePossibleMax(): Int {
            val d = this.data.flatten().toMutableList()
            var i = 0
            d.removeAll(listOf(0))
            while (d.containDuplicated()) {
                d.sort()
                i = 0
                while (i < d.size) {
                    if ((d.tryToGet(i)?.toInt() ?: Float.NaN) == (d.tryToGet(i + 1)?.toInt() ?: Float.NaN)) {
                        d[i] = d[i] * 2
                        d.removeAt(i + 1)
                    }
                    i++
                }
            }
            return d.maxOrNull() ?: 0
        }

        fun evaluateContinuable(): Boolean{
            val d = this.data.flatten().toMutableList()
            d.removeAll(listOf(0))
            return d.containDuplicated()
        }


        fun maxNum(): Int {
            var max:Int = data[0][0]
            for (i in 0..<4) {
                for (j in 0..<4) {
                    if (data[i][j] > max) {
                        max = data[i][j]
                    }
                }
            }
            return max
        }
    }

    var history = HashSet<Int>()
    fun dfs(last: Grid, ttl: Int = 0): Int {
        var maxNum = last.maxNum()

        when {
            !history.add(last.hashCode()) -> return maxNum
            !last.evaluateContinuable() -> return maxNum
            last.evaluatePossibleMax() == maxNum -> return maxNum
        }

        val originalData = Grid(last)

        for (i in Direction.entries){
            var currentData = Grid(last)
            currentData.moveAll(i)
            if (originalData != currentData) {
                maxNum = max(maxNum, dfs(currentData, ttl + 1))
            }
        }
        return maxNum
    }

    fun <T> Collection<T>.tryToGet(index: Int): T? {
        return try {
            this.elementAt(index)
        } catch (_: IndexOutOfBoundsException) {
            return null
        }
    }
    fun <T> Collection<T>.containDuplicated(): Boolean {
        val s = HashSet<T>()
        for (i in this) {
            if (!s.add(i)) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val sc = Scanner(System.`in`)
        val result = mutableListOf<Int>()

        val tmp = Array<Array<Int>>(5) { Array(16) { 0 } }

        for (i in 0 until 5) {
            for (j in 0 until 16) {
                tmp[i][j] = sc.nextInt()
            }
        }
        val t=System.currentTimeMillis()

        for (i in 0 until 5) {
            result.add(dfs(Grid(tmp[i]), 0))
            history = HashSet<Int>()
        }

        result.forEach { println(it) }

        println(System.currentTimeMillis()-t)
    }
}
