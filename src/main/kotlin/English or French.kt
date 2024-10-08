package org.example

object EnglishorFrench {
    @JvmStatic
    fun main(args: Array<String>) {
        val n = readln().toInt()
        var t = 0
        var s = 0
        repeat(n) {
            readLine()!!.toLowerCase().forEach {
                when (it) {
                    't' -> t++
                    's' -> s++
                }
            }
        }
        println(if (t > s) "English" else "French")
    }
}