

import org.example.`16 BIT SW ONLY`
import org.example.`2048`.Grid
import org.example.`2048`.dfs
import java.util.Base64
import java.util.Scanner
import kotlin.test.Test


class Test {
    @Test fun `1`(){
        `16 BIT SW ONLY`()
    }
    @Test fun `2`(){
        var data: ArrayList<Int> = arrayListOf()
        """
0 2 2 0 
2 0 0 8 
0 8 2 32 
2 4 0 2
            
            
        """.trimIndent().let { it.split(' ', '\n',',').map{;map@it.toIntOrNull()}.forEach { it?.let { data.add(it) } } }

        Grid(data).also {
            println(it)
            println(
                dfs(it)
            )
        }
    }


    /*
    * [l, r, u, r, d, l, u, d, r, u, l, r, d]
    * [l, u, l, d, l, r, u, l, d, r, u, l, u, d, l, r, u, l, u, d, r, u, r]
    * [r, u, l, d, r, u, l, r, u, d, l, u, r, d, l, u, r, d, l, u, l, r, d, l, r, u, d, l, u, r, u, r, d]
    * [l, r, u, d, l, u, r, d]
    * [u, d, l, r, u, l, u, r, d, l, u, r, u, l, r, d, r]
    *
    *
    * */
    @Test fun decode(){
        val os = "ARAQAwMVEgElECIQAjAmACIgYAEAQFIQEDAwAwQSAwMUZSAREAEQAA=="
        println(os)
        val s = Base64.getDecoder().decode(os).joinToString("") { "%02x".format(it) }
        val sb = StringBuilder()
        for (i in s){
            when(i){
                '0'->sb.append("0 ")
                '1'->sb.append("2 ")
                '2'->sb.append("4 ")
                '3'->sb.append("8 ")
                '4'->sb.append("16 ")
                '5'->sb.append("32 ")
                '6'->sb.append("64 ")
                '7'->sb.append("128 ")
                '8'->sb.append("256 ")
                '9'->sb.append("512 ")
                'a'->sb.append("1024 ")
                'b'->sb.append("2048 ")
                'c'->sb.append("4096 ")
                'd'->sb.append("8192 ")
                'e'->sb.append("16384 ")
                'f'->sb.append("32768 ")
            }
        }
        println(sb)
        val sc = Scanner(sb.toString().byteInputStream())
        val tmp = Array<Array<Int>>(5) { Array(16) { 0 } }

        for (i in 0 until 5) {
            for (j in 0 until 16) {
                tmp[i][j] = sc.nextInt()
            }
        }
        val result = mutableListOf<Int>()
        for (i in 0 until 5) {
            val g=Grid(tmp[i])
            println(g)
            result.add(dfs(g).apply { println(this.second) }.first)

        }
        result.forEach { print(it);print(" ") }
    }
}