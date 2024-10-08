package org.example

object APlusB {
    @JvmStatic
    fun main(args: Array<String>) {
        val r = readln().toInt()
        repeat(r) {
            val (a, b) = readln().split(" ").map { it.toInt() }
            println(a + b)
        }
    }
}