package org.example

object WhoIsInTheMiddle{
    @JvmStatic
    fun main(args: Array<String>) {
        val arr = arrayOf(readln().toInt(), readln().toInt(), readln().toInt())
        println(arr.sorted()[1])
    }
}