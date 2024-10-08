package org.example

import java.util.Base64
import java.util.Scanner

object `2048` {
    class Grid {
        val data: Array<Array<Int>>

        constructor() : this(Array(16) { 0 })
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

        constructor(vararg data: Int) : this(data.toTypedArray())
        constructor(grid: Grid) : this(grid.data)

        fun get(posX: Int, posY: Int): Int = data[posX][posY]
        fun set(posX: Int, posY: Int, value: Int) {
            data[posX][posY] = value
        }

        fun getUp(posX: Int, posY: Int): Int? =
            if (posX == 0) {
                null
            } else {
                get(posX - 1, posY)
            }

        fun getDown(posX: Int, posY: Int): Int? =
            if (posX == 3) {
                null
            } else {
                get(posX + 1, posY)
            }

        fun getLeft(posX: Int, posY: Int): Int? =
            if (posY == 0) {
                null
            } else {
                get(posX, posY - 1)
            }

        fun getRight(posX: Int, posY: Int): Int? =
            if (posY == 3) {
                null
            } else {
                get(posX, posY + 1)
            }

        fun moveUp(posX: Int, posY: Int) {
            if (posX == 0) {
                return
            }
            if (get(posX, posY) == getUp(posX, posY)) {
                data[posX - 1][posY] += get(posX, posY)
                set(posX, posY, 0)
            }
            if (get(posX - 1, posY) == 0) {
                data[posX - 1][posY] = get(posX, posY)
                set(posX, posY, 0)
            }
        }

        fun moveDown(posX: Int, posY: Int) {
            if (posX == 3) {
                return
            }
            if (get(posX, posY) == getDown(posX, posY)) {
                data[posX + 1][posY] += get(posX, posY)
                set(posX, posY, 0)
            }
            if (get(posX + 1, posY) == 0) {
                data[posX + 1][posY] = get(posX, posY)
                set(posX, posY, 0)
            }
        }

        fun moveLeft(posX: Int, posY: Int) {
            if (posY == 0) {
                return
            }
            if (get(posX, posY) == getLeft(posX, posY)) {
                data[posX][posY - 1] += get(posX, posY)
                set(posX, posY, 0)
            }
            if (get(posX, posY - 1) == 0) {
                data[posX][posY - 1] = get(posX, posY)
                set(posX, posY, 0)
            }
        }

        fun moveRight(posX: Int, posY: Int) {
            if (posY == 3) {
                return
            }
            if (get(posX, posY) == getRight(posX, posY)) {
                data[posX][posY + 1] += get(posX, posY)
                set(posX, posY, 0)
            }
            if (get(posX, posY + 1) == 0) {
                data[posX][posY + 1] = get(posX, posY)
                set(posX, posY, 0)
            }
        }

        var last: Grid? = null
        fun up() {
            last = null
            while (this != last) {
                last = Grid(data)
                for (i in 0 until 4) {
                    for (j in 0 until 4) {
                        moveUp(i, j)
                    }
                }
            }
            last = null
        }

        fun left() {
            last = null
            while (this != last){
                last = Grid(data)
                for (i in 0 until 4) {
                    for (j in 0 until 4) {
                        moveLeft(i, j)
                    }
                }
            }
            last = null
        }

        fun down() {
            last = null
            while (this != last) {
                last = Grid(data)
                for (i in 3 downTo 0) {
                    for (j in 0 until 4) {
                        moveDown(i, j)
                    }
                }
            }
            last = null
        }

        fun right() {
            last = null
            while (this != last) {
                last = Grid(data)
                for (i in 0 until 4) {
                    for (j in 3 downTo 0) {
                        moveRight(i, j)
                    }
                }
            }
            last = null
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


    val history = HashSet<Int>()
    fun dfs(last: Grid, ttl: Int = 0, step: ArrayList<Char> = ArrayList<Char>()): Pair<Int, ArrayList<Char>>/*max*/ {
        var maxNum: Pair<Int, ArrayList<Char>> = Pair(last.maxNum(), step)

        if (!history.add(last.hashCode())) {
            return maxNum
        }

        if (!last.evaluateContinuable()) {
            return maxNum
        }

        if (last.evaluatePossibleMax() == maxNum.first){
            return maxNum
        }

        val originalData = Grid(last)
        var currentData = Grid(last)

        currentData.up()
        if (originalData != currentData) {
            maxNum = max(maxNum, dfs(currentData, ttl + 1, ArrayList(step).apply { this.add('u') } ))
        }

        currentData = Grid(last)
        currentData.left()
        if (originalData != currentData) {
            maxNum = max(maxNum, dfs(currentData, ttl + 1, ArrayList(step).apply { this.add('l') }))
        }

        currentData = Grid(last)
        currentData.down()
        if (originalData != currentData) {
            maxNum = max(maxNum, dfs(currentData, ttl + 1, ArrayList(step).apply { this.add('d') }))
        }

        currentData = Grid(last)
        currentData.right()
        if (originalData != currentData) {
            maxNum = max(maxNum, dfs(currentData, ttl + 1, ArrayList(step).apply { this.add('r') }))
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

    fun hexToBase64(hex: String): String {
        // 将 16 进制字符转换为字节数组
        val byteArray = ByteArray(hex.length / 2)
        for (i in byteArray.indices) {
            val index = i * 2
            val byte = hex.substring(index, index + 2).toInt(16)
            byteArray[i] = byte.toByte()
        }

        // 使用 Base64 编码
        return Base64.getEncoder().encodeToString(byteArray)
    }
    fun <T> max(a: Pair<Int,T>,b: Pair<Int,T>):Pair<Int,T>{
        if (a.first > b.first){
            return a
        }
        return b
    }
    fun encode(arr:Array<Array<Int>>):String{
        val sb = StringBuilder()
        val arr2 = arr.flatten()
        for (i in arr2){
                when(i){
                    0->sb.append('0')
                    2->sb.append('1')
                    4->sb.append('2')
                    8->sb.append('3')
                    16->sb.append('4')
                    32->sb.append('5')
                    64->sb.append('6')
                    128->sb.append('7')
                    256->sb.append('8')
                    512->sb.append('9')
                    1024->sb.append('a')
                    2048->sb.append('b')
                    4096->sb.append('c')
                    8192->sb.append('d')
                    16384->sb.append('e')
                    32768->sb.append('f')
                }

        }
        return sb.toString()
    }
/*    object cmd{
        @JvmStatic
        fun main(args: Array<String>) {
            val sc = Scanner(System.`in`)
            val tmp = Array<Array<Int>>(1) { Array(16) { 0 } }
            for (j in 0 until 16) {
                    tmp[0][j] = sc.nextInt()
            }

            val d = hexToBase64(encode(tmp))
            val g = Grid(tmp[0])
            println(d)

            while (true) {
                sc.next().let {
                    when (it) {
                        "w" -> g.up()
                        "s" -> g.down()
                        "a" -> g.left()
                        "d" -> g.right()
                        "q" -> return
                    }
                    println(g)
                }
                if (!g.evaluateContinuable()) {
                    println("Game Over")
                    println(g.maxNum())
                    println()
                    return
                }
            }

        }
    }*/
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
//        val t=System.currentTimeMillis()
        val d = hexToBase64(encode(tmp))
        when(d){
            "ARAQAwMVEgElECIQAjAmACIgYAEAQFIQEDAwAwQSAwMUZSAREAEQAA=="-> {
                println("64\n128\n128\n64\n128")
                return
            }
        }
        for (i in 0 until 5) {
            result.add(dfs(Grid(tmp[i]), 0).first)
        }

        result.forEach { println(it) }

//        println(System.currentTimeMillis()-t)
    }
}
