package org.example

object TriangleTimes {
    @JvmStatic
    fun main(args: Array<String>) {
        val a = readln().toInt()
        val b = readln().toInt()
        val c = readln().toInt()
        println(
            if (a == 60 && b==60&&c==60)"Equilateral"
            else if (a+b+c==180 && (a==b||b==c||a==c))"Isosceles"
            else if (a+b+c==180)"Scalene"
            else "Error"
        )
    }
}