package org.example.utils


object FastReader {
    fun nextInt(): Int {
        fun read(): Byte? {
            val r = System.`in`.read()
            return if (r == -1) null else r.toByte()
        }
        var c: Byte
        var result: Int = 0
        var negativeFlag = false

        do {
            c = read() ?: return 0
        } while (c.toChar().isWhitespace())

        if (c.toChar() == '-') {
            negativeFlag = true
            c = read() ?: return 0
        }

        while (true) {
            val tmp = c.toChar().digitToIntOrNull() ?: break
            result = result * 10 + tmp
            c = read() ?: break
        }

        return if (negativeFlag) -result else result
    }
}

fun main() {
    var t = System.currentTimeMillis()
    println(System.currentTimeMillis() - t)
    val n = FastReader.nextInt()
    val m = FastReader.nextInt()
    println(n + m)  // 示例：将两个输入整数相加

    //compare
    t = System.currentTimeMillis()
    val sc = java.util.Scanner(System.`in`)
    println(System.currentTimeMillis() - t)
    val a = sc.nextInt()
    val b = sc.nextInt()
    println(a + b)
}
