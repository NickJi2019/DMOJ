package org.example

import java.util.Scanner


fun `16 BIT SW ONLY`() {
    val result = mutableListOf<Boolean>()
    val sc = Scanner(System.`in`)
    for(i in 1..sc.nextInt()){
        val a = sc.nextBigInteger()
        val b = sc.nextBigInteger()
        val c = sc.nextBigInteger()
        result.add(a*b==c)
    }
    result.forEach {
        if (it){
            println("POSSIBLE DOUBLE SIGMA")
        }else{
            println("16 BIT S/W ONLY")
        }
    }
}
fun main() {
    `16 BIT SW ONLY`()
}