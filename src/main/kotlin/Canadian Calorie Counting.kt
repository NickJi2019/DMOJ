package org.example

object CanadianCalorieCounting {
    @JvmStatic
    fun main(args: Array<String>) {
        val burger = when(readln().toInt()){
            1 -> 461
            2 -> 431
            3 -> 420
            4 -> 0
            else -> 0
        }
        val side = when(readln().toInt()){
            1 -> 100
            2 -> 57
            3 -> 70
            4 -> 0
            else -> 0
        }
        val drink = when(readln().toInt()){
            1 -> 130
            2 -> 160
            3 -> 118
            4 -> 0
            else -> 0
        }
        val dessert = when(readln().toInt()){
            1 -> 167
            2 -> 266
            3 -> 75
            4 -> 0
            else -> 0
        }
        println("Your total Calorie count is ${burger+side+drink+dessert}.")
    }
}