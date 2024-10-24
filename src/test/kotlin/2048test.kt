import org.example.`2048`
import org.example.`2048`.Grid
import java.util.Base64
import java.util.Scanner

object `2048test` {
    fun hexToBase64(hex: String): String {
        val byteArray = ByteArray(hex.length / 2)
        for (i in byteArray.indices) {
            byteArray[i] = hex.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
        return Base64.getEncoder().encodeToString(byteArray)
    }
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

    fun encode(arr: Array<Array<Int>>): String {
        val sb = StringBuilder()
        val arr2 = arr.flatten()
        for (i in arr2) {
            sb.append(when (i) {
                0 -> '0';2 -> '1';4 -> '2';8 -> '3';16 -> '4';32 -> '5';64 -> '6';128 -> '7';256 -> '8';512 -> '9'
                1024 -> 'a';2048 -> 'b';4096 -> 'c';8192 -> 'd';16384 -> 'e';32768 -> 'f'
                else -> '?'
            })

        }
        return sb.toString()
    }
}
/*0 2 0 4
8 0 0 0
0 64 0 0
4 16 32 2

[l, u, d, r, u, l, d, r, u, l, d, l, r, d]
32 4 32 2
2 2 0 8
16 0 2 2
8 16 2 0

[l, u, d, l, r, u, r, d, l, u, l, r, u, l, d, r, u, l, u, d, r, u, l, d, l, r, d]
2 2 2 2
8 0 2 2
32 8 64 0
0 2 0 2

[r]
16 0 0 0
0 8 4 2
0 16 4 0
2 8 4 0

[u, d, l, r, u, l, d, r, u, l, u, d, r, u, r]
0 8 2 0
0 4 32 0
4 0 2 2
0 2 0 8

[r]*/