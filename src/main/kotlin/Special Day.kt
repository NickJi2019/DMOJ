package org.example

import java.util.GregorianCalendar

object SpecialDay {
    @JvmStatic
    fun main(args: Array<String>) {
        val m = readln().toInt()
        val d = readln().toInt()
        val date = GregorianCalendar(2015,m,d)
        val sp = GregorianCalendar(2015,2,18)
        println(if (date.before(sp)) "Before" else if (date.after(sp)) "After" else "Special")
    }
}