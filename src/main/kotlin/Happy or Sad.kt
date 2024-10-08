package org.example

object HappyorSad {
    @JvmStatic
    fun main(args: Array<String>) {
        val input = readLine()!!
        val happy = input.split(":-)").size-1
        val sad = input.split(":-(").size-1
        if (happy == 0 && sad == 0) {
            println("none")
        } else if (happy == sad) {
            println("unsure")
        } else if (happy > sad) {
            println("happy")
        } else {
            println("sad")
        }
    }
}