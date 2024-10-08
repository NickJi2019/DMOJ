package org.example

import java.util.Scanner
import kotlin.math.max

class Grid{
    val data:Array<Array<Int>>

    constructor():this(Array(16){0})
    constructor(data: Array<Int>){
        this.data = arrayOf(
            data.copyOfRange(0,4),
            data.copyOfRange(4,8),
            data.copyOfRange(8,12),
            data.copyOfRange(12,16)
        )
    }
    constructor(data: List<Int>):this(data.toTypedArray())
    constructor(data:Array<Array<Int>>){
        this.data = Array<Array<Int>>(4){
            data[it].copyOf()
        }
    }
    constructor(vararg data: Int):this(data.toTypedArray())
    constructor(grid: Grid):this(grid.data)

    fun get(posX: Int, posY: Int): Int = data[posX][posY]
    fun set(posX: Int, posY: Int, value: Int){ data[posX][posY] = value}
    fun getUp(posX:Int, posY: Int):Int? =
        if (posX == 0){ null }
        else{ get(posX-1,posY) }

    fun getDown(posX: Int,posY: Int): Int? =
        if (posX == 3){ null }
        else{ get(posX+1,posY) }

    fun getLeft(posX: Int,posY: Int): Int? =
        if (posY == 0){ null }
        else{ get(posX,posY-1) }

    fun getRight(posX: Int,posY: Int): Int? =
        if (posY == 3){ null }
        else{ get(posX,posY+1) }

    fun moveUp(posX: Int, posY: Int){
        if (posX == 0){
            return
        }
        if (get(posX, posY) == getUp(posX, posY)) {
            data[posX-1][posY] += get(posX,posY)
            set(posX,posY,0)
        }
        if (get(posX-1,posY) == 0){
            data[posX-1][posY] = get(posX,posY)
            set(posX,posY,0)
        }
    }
    fun moveDown(posX: Int, posY: Int){
        if (posX == 3){
            return
        }
        if (get(posX, posY) == getDown(posX, posY)) {
            data[posX+1][posY] += get(posX,posY)
            set(posX,posY,0)
        }
        if (get(posX+1,posY) == 0){
            data[posX+1][posY] = get(posX,posY)
            set(posX,posY,0)
        }

    }
    fun moveLeft(posX: Int, posY: Int){
        if (posY == 0){
            return
        }
        if (get(posX, posY) == getLeft(posX, posY)) {
            data[posX][posY-1] += get(posX,posY)
            set(posX,posY,0)
        }
        if (get(posX,posY-1) == 0){
            data[posX][posY-1] = get(posX,posY)
            set(posX,posY,0)
        }

    }
    fun moveRight(posX: Int, posY: Int){
        if (posY == 3){
            return
        }
        if (get(posX, posY) == getRight(posX, posY)) {
            data[posX][posY+1] += get(posX,posY)
            set(posX,posY,0)
        }
        if (get(posX,posY+1) == 0){
            data[posX][posY+1] = get(posX,posY)
            set(posX,posY,0)
        }

    }

    fun up(){
        var last : Grid
        do{
            last = Grid(data.flatten().toTypedArray())
            for (i in 0 until 4) {
                for (j in 0 until 4) {
                    moveUp(i, j)
                }
            }
        }while (this != last)
    }
    fun down(){
        var last : Grid
        do{
            last = Grid(data.flatten().toTypedArray())
            for (i in 3 downTo 0) {
                for (j in 0 until 4) {
                    moveDown(i, j)
                }
            }
        }while (this != last)
    }
    fun left(){
        var last : Grid
        do{
            last = Grid(data.flatten().toTypedArray())
            for (i in 0 until 4) {
                for (j in 0 until 4) {
                    moveLeft(i, j)
                }
            }
        }while (this != last)
    }
    fun right(){
        var last : Grid
        do{
            last = Grid(data.flatten().toTypedArray())
            for (i in 0 until 4) {
                for (j in 3 downTo 0) {
                    moveRight(i, j)
                }
            }
        }while (this != last)
    }

    override fun toString(): String {
        var a = ""
        this.data.forEach { a+=it.joinToString(" "); a+="\n" }
        return a
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Grid){
            this.toString() == other.toString()
        }else{
            false
        }
    }

    fun evaluatePossibleMax():Int{
        val d = this.data.flatten().toMutableList()
        d.removeAll(listOf(0))
        var i = 0
        while (i <= d.size){
            d.sort()
            var j = 0
            while (j < d.size){
                if (d.tryToGet(j) == d.tryToGet(j+1) && (d.tryToGet(j) != null&&d.tryToGet(j+1) != null)){
                    d[j] = d[j]*2
                    d.removeAt(j+1)
                }
                j++
            }
            i++
        }
        return d.maxOrNull()?:0
    }
    fun evaluateContinuable(): Boolean{
        val d = this.data.flatten().toMutableList()
        d.removeAll(listOf(0))
        val s = HashSet<Int>()
        for(i in d){
            if (!s.add(i)){
                return true
            }
        }
        return false
    }
    fun maxNum():Int{
        return this.data.flatten().maxOrNull()?:0
    }

    override fun hashCode(): Int {
        return data.contentDeepHashCode()
    }
}


val history = ArrayList<Grid>()
fun dfs(last: Grid, ttl:Int): Int/*max*/{
    val originalData = Grid(last)
    var currentData = Grid(last)
    var maxNum = currentData.maxNum()

    for (i in history){
        if (i == originalData){
//            println("c")
            return dfs@maxNum
        }
    }

    history.add(originalData)
    if (!currentData.evaluateContinuable()){
        return maxNum
    }

    currentData.up()
//    println("u")
    if (originalData != currentData){
        maxNum = max(maxNum, dfs(currentData, ttl+1))
//        println("us")
    }
    else{
//        println("uns")
    }

    currentData = Grid(last)
    currentData.left()
//    println("l")
    if (originalData != currentData){
        maxNum = max(maxNum, dfs(currentData, ttl+1))
//        println("ls")
    } else{
//        println("lns")
    }

    currentData = Grid(last)
    currentData.down()
//    println("d")
    if (originalData != currentData) {
        maxNum = max(maxNum, dfs(currentData, ttl + 1))
//        println("ds")
    } else{
//        println("dns")
    }

    currentData = Grid(last)
    currentData.right()
//    println("r")
    if (originalData != currentData){
        maxNum = max(maxNum, dfs(currentData, ttl+1))
//        println("rs")
    } else{
//        println("rns")
    }


    return maxNum
}

fun <T> Collection<T>.tryToGet(index: Int): T?{
    return try{
        this.elementAt(index)
    }catch (_: IndexOutOfBoundsException){
        return null
    }
}


fun main() {
    val sc = Scanner(System.`in`)
    val result = mutableListOf<Int>()
    val tmp = Array<Array<Int>>(5) { Array(16) { 0 } }

    for (i in 0 until 5) {
        for (j in 0 until 16) {
            tmp[i][j] = sc.nextInt()
        }
    }
    val t=System.currentTimeMillis()

    for (i in 0 until 5) {
        result.add(dfs(Grid(tmp[i]),0))
    }

    result.forEach { println(it) }
    println(System.currentTimeMillis()-t)
}
