

import `2048test`.hexToBase64
import org.example.`16 BIT SW ONLY`
import org.example.`2048`
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



    @Test fun decode(){
        val os = "AQIwAAYAJFFSUREDQBE0EBERMBFTYAEBQAADIQQgEyADEAJQIBEBAw=="
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
            result.add(dfs(g).apply { println(this) })

        }
        result.forEach { print(it);print(" ") }
    }
}
    object cmd{
        @JvmStatic
        fun main(args: Array<String>) {
            val sc = Scanner(System.`in`)
            val tmp = Array<Array<Int>>(1) { Array(16) { 0 } }
            for (j in 0 until 16) {
                    tmp[0][j] = sc.nextInt()
            }

            val d = hexToBase64(encode(tmp))
            val g = Grid(tmp[0])
            println(d)
                        while (true) {
                sc.next().let {
                    when (it) {
                        "w" -> g.moveAll(`2048`.Direction.UP)
                        "s" -> g.moveAll(`2048`.Direction.DOWN)
                        "a" -> g.moveAll(`2048`.Direction.LEFT)
                        "d" -> g.moveAll(`2048`.Direction.RIGHT)
                        "q" -> return
                    }
                    println(g)
                }
                if (!g.evaluateContinuable()) {
                    println("Game Over")
                    println(g.maxNum())
                    println()
                    return
                }
            }

        }
        fun encode(arr:Array<Array<Int>>):String{
            val sb = StringBuilder()
            val arr2 = arr.flatten()
            for (i in arr2){
                when(i){
                    0->sb.append('0')
                    2->sb.append('1')
                    4->sb.append('2')
                    8->sb.append('3')
                    16->sb.append('4')
                    32->sb.append('5')
                    64->sb.append('6')
                    128->sb.append('7')
                    256->sb.append('8')
                    512->sb.append('9')
                    1024->sb.append('a')
                    2048->sb.append('b')
                    4096->sb.append('c')
                    8192->sb.append('d')
                    16384->sb.append('e')
                    32768->sb.append('f')
                }

            }
            return sb.toString()
        }
    }
