package org.example

object WinningScore {
    @JvmStatic
    fun main(args: Array<String>) {
        val score = Array(6) { readln().toInt() }
        val apple = score[0] * 3 + score[1] * 2 + score[2]
        val banana = score[3] * 3 + score[4] * 2 + score[5]
        println(
            when {
                apple > banana -> "A"
                apple < banana -> "B"
                else -> "T"
            }
        )
    }
}