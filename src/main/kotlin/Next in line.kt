package org.example


object NextInLine{
    @JvmStatic fun main(args: Array<String>) {
        val a = readLine()!!.toInt()
        val b = readLine()!!.toInt()
        println(b + (b - a))
    }
}