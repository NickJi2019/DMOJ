package org.example

import java.util.Scanner
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

// 工具属性与函数
typealias Real = Double
fun Scanner.nextReal(): Real = this.nextDouble()
fun Number.toReal(): Real = this.toDouble()

// 数据结构类
data class Point(var x: Real, var y: Real) // 二维坐标
data class Vector(var deltaX: Real, var deltaY: Real) { // 二维向量
    fun norm(): Real = sqrt(deltaX.pow(2) + deltaY.pow(2)) // 求模
}
data class Pair<A, B>(var first: A, var second: B) { // 二元组
    override fun toString(): String = "($first, $second)"
}

data class Nemo(
    var weight: Real,
    var vitesseLimit: Real,
    var pos: Point
)

data class Shrimp(
    val id: Int,
    val weight: Real,
    var pos: Point,
    val path: Vector,
    var eaten: Boolean = false
) {
    val vitesse: Real = path.norm() // 速度
    override fun toString(): String {
        return "Shrimp(id=$id, eaten=$eaten, weight=$weight, pos=$pos, path=$path, vitesse=$vitesse)"
    }
}

data class EatRecord(// 进食记录
    val time: Real?,
    val pos: Point?,
    val id: Int
)

data class NemoShrimpStatus(// 用于记录两者的状态
    val nemoPos: Point,
    val shrimpPos: Point,
    val shrimpPath: Vector,
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NemoShrimpStatus) return false
        return this.hashCode() == other.hashCode()
    }

    override fun hashCode(): Int {
        var result = nemoPos.hashCode()
        result = 31 * result + shrimpPos.hashCode()
        result = 31 * result + shrimpPath.hashCode()
        return result
    }
}

/*
*  **海洋类**
*
*  用于描述海洋的状态，包括时间、时间限制、Shrimps、Nemo
* */
class Sea {

    // 属性
    val time: Real // 本海洋实例的时间
    val timeLimit: Real // 时间限制
    val shrimps: Array<Shrimp?> // 虾群

    val nemo: Nemo

    /*
    * 静态属性
    * */
    companion object{
        object ShrimpIsEatenException : Exception()
        object TimeLimitExceededException : Exception()

        object UnableToCatchUpException : Exception()
        /*
        * 缓存相遇时间
        * @see calculateTime
        * */
        val timeCache : HashMap<NemoShrimpStatus,Real> = hashMapOf()
        /*
        * 缓存交点
        * @see calculateIntersection
        * */
        val intersectionCache: HashMap<NemoShrimpStatus,Point> = hashMapOf()
    }

    /*
    * @Constructor 默认构造函数
    *
    * @param shrimps: Array<Shrimp?> 虾群
    * @param time: Real 时间
    * @param timeLimit: Real 时间限制
    * @param nemoPos: Point Nemo位置
    * @param nemoWeight: Real Nemo体重
    * @param nemoVitesseLimit: Real Nemo速度限制
    * */
    constructor(shrimps: Array<Shrimp?>, time: Real, timeLimit: Real, nemoPos: Point, nemoWeight: Real, nemoVitesseLimit: Real) {
        this.shrimps = shrimps.map { it?.copy() }.toTypedArray() //深拷贝
        this.time = time
        this.timeLimit = timeLimit
        this.nemo = Nemo(nemoWeight, nemoVitesseLimit, nemoPos)
    }
    /*
    * @Constructor 用于复制同时进行修改的构造函数
    *
    * @param s: Sea 海洋实例
    * @param shrimps: Array<Shrimp?> 虾群（默认从s拷贝）
    * @param time: Real 时间（默认从s拷贝）
    * @param timeLimit: Real 时间限制（默认从s拷贝）
    * @param nemoPos: Point Nemo位置（默认从s拷贝）
    * @param nemoWeight: Real Nemo体重（默认从s拷贝）
    * @param nemoVitesseLimit: Real Nemo速度限制（默认从s拷贝）
    * */
    constructor(
        s: Sea,
        shrimps: Array<Shrimp?> = s.shrimps,
        time: Real = s.time,
        timeLimit: Real = s.timeLimit,
        nemoPos: Point = s.nemo.pos,
        nemoWeight: Real = s.nemo.weight,
        nemoVitesseLimit: Real = s.nemo.vitesseLimit
    ) : this(shrimps, time, timeLimit, nemoPos, nemoWeight, nemoVitesseLimit)

    // 重载equals
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Sea) return false

        if (time != other.time) return false
        if (nemo.pos != other.nemo.pos) return false
        if (nemo.weight != other.nemo.weight) return false
        if (shrimps.size != other.shrimps.size) return false

        for (i in shrimps.indices) {
            val s1 = shrimps[i]
            val s2 = other.shrimps[i]
            if (s1?.eaten != s2?.eaten) return false
        }
        return true
    }
    // 重载hashCode
    override fun hashCode(): Int {
        var result = time.hashCode()
        result = 31 * result + nemo.pos.hashCode()
        result = 31 * result + nemo.weight.hashCode()
        for (shrimp in shrimps) {
            result = 31 * result + (shrimp?.eaten?.hashCode() ?: 0)
        }
        return result
    }

    /*
    * 吃虾并前进至下一个时间点
    *
    * @param shrimp: Shrimp? 虾
    * @return Pair<Sea, EatRecord> 下一个海洋实例与进食记录
    * @throws ShrimpIsEatenException 虾已被吃
    * @throws TimeLimitExceededException 时间超限
    * @throws UnableToCatchUpException 无法追上
    * @see calculateTime
    * @see calculateIntersection
    * @see moveToTime
    * */
    fun eatShrimp(shrimp: Shrimp?): Pair<Sea, EatRecord> {
        shrimp ?: throw ShrimpIsEatenException
        val timeUsed = calculateTime(shrimp)

        when {
            shrimp.eaten -> throw ShrimpIsEatenException
            timeUsed == Real.POSITIVE_INFINITY -> throw TimeLimitExceededException
            timeUsed + this.time > timeLimit -> throw UnableToCatchUpException
        }

        shrimp.eaten = true

        moveToTime(timeUsed)

        return Pair(
            Sea(this, time = this.time + timeUsed, nemoPos = calculateIntersection(shrimp), nemoWeight =  nemo.weight + shrimp.weight),
            EatRecord(this.time + timeUsed, calculateIntersection(shrimp), shrimp.id)
        )
    }

    /*
    * 前进至下一个时间点
    *
    * @param t: Real 时间
    * */
    fun moveToTime(t: Real){
        for (i in shrimps){
            i ?: continue
            if (i.eaten) continue
            i.pos.x = i.pos.x + i.path.deltaX * t
            i.pos.y = i.pos.y + i.path.deltaY * t
        }
    }

    /*
    * 计算经济性
    * 经济性 = 体重 / 时间^2
    *
    * @param shrimp: Shrimp 虾
    * */
    fun calculateEconomic(shrimp: Shrimp): Real {
        val time = calculateTime(shrimp)
        return if (time == 0.toReal()) Real.POSITIVE_INFINITY else shrimp.weight / (time * time)
    }

    /*
    * 计算时间
    * 通过求解二次方程来计算时间
    * 二次方程：a * t^2 + b * t + c = 0
    * a = vX^2 + vY^2 - V^2
    * b = 2 * (vX * deltaX + vY * deltaY)
    * c = deltaX^2 + deltaY^2
    *
    * @param shrimp: Shrimp 虾
    * @return Real 时间
    * */
    fun calculateTime(shrimp: Shrimp): Real {
        val relation = NemoShrimpStatus(nemo.pos, shrimp.pos, shrimp.path)
        if (timeCache.contains(relation)) return timeCache[relation]!!

        if (shrimp.vitesse == 0.toReal()) {
            val t = (nemo.pos.x.pow(2) + nemo.pos.y.pow(2)).pow(0.5.toReal()) / nemo.vitesseLimit
            timeCache.put(relation, t)
            return t
        }

        val deltaX = shrimp.pos.x - nemo.pos.x
        val deltaY = shrimp.pos.y - nemo.pos.y
        val vX = shrimp.path.deltaX
        val vY = shrimp.path.deltaY
        val V = nemo.vitesseLimit

        // 计算二次方程的系数
        val a = vX * vX + vY * vY - V * V
        val b = 2.0 * (vX * deltaX + vY * deltaY)
        val c = deltaX * deltaX + deltaY * deltaY

        // 定义可接受的误差
        val epsilon = 1e-6

        if (abs(a) < epsilon) {
            if (abs(b) < epsilon) {
                return if (abs(c) < epsilon) 0.0 else Real.POSITIVE_INFINITY
            }
            val t = -c / b
            return if (t >= -epsilon) maxOf(t, 0.0) else Real.POSITIVE_INFINITY
        }
        // 判别式
        val discriminant = b * b - 4.0 * a * c

        if (discriminant < -epsilon) {
            return Double.POSITIVE_INFINITY
        }

        val sqrtDiscriminant = if (discriminant < 0.0) 0.0 else sqrt(discriminant)
        var t1 = (-b - sqrtDiscriminant) / (2.0 * a)
        var t2 = (-b + sqrtDiscriminant) / (2.0 * a)

        val possibleTimes = arrayOf(t1, t2).filter { it >= -epsilon }.map { maxOf(it, 0.0) }
        val result =  if (possibleTimes.isEmpty()) Real.POSITIVE_INFINITY else possibleTimes.minOrNull()!!

        timeCache.put(
            NemoShrimpStatus(nemo.pos, shrimp.pos, shrimp.path),
            result
        )
        return result
    }

    /*
    * 计算交点
    *
    * @param shrimp: Shrimp 虾
    * @return Point 交点
    * @see calculateTime
    * */
    fun calculateIntersection(shrimp: Shrimp): Point {
        val relation = NemoShrimpStatus(nemo.pos, shrimp.pos, shrimp.path).apply {
            if (intersectionCache.contains(this)) return calculateIntersection@intersectionCache[this]!!
        }

        val time = calculateTime(shrimp)
        if (time == Real.POSITIVE_INFINITY) {
            intersectionCache.put(relation, Point(Real.NaN, Real.NaN))
            return Point(Real.NaN, Real.NaN)
        }
        return Point(shrimp.pos.x + shrimp.path.deltaX * time, shrimp.pos.y + shrimp.path.deltaY * time)
    }
}


fun dfs(
    sea: Sea,
    record: Pair<Real, ArrayList<EatRecord>>
): Pair<Real, ArrayList<EatRecord>> {
    val getShrimp = {s:Sea,i:Int -> s.shrimps[i] }
    var bestRecord: Pair<Real, ArrayList<EatRecord>> = record

    for (i in sea.shrimps.indices) {

        if (getShrimp(sea,i) == null || getShrimp(sea,i)?.eaten == true) continue
        if (sea.shrimps.all { it == null || it.eaten }) break

        val nowSea = Sea(sea)
        var nowRecord = Pair(record.first, ArrayList(record.second))
        var next: Sea
        var rec: EatRecord

        try {
            var (n, r) = nowSea.eatShrimp(getShrimp(nowSea,i))
            next = n;rec = r
        } catch (e: Sea.Companion.ShrimpIsEatenException) {
            continue
        } catch (e: Sea.Companion.TimeLimitExceededException) {
            continue
        } catch (e: Sea.Companion.UnableToCatchUpException) {
            continue
        }

        nowRecord.first = next.nemo.weight
        nowRecord.second.add(rec)

        nowRecord = dfs(next, nowRecord)

        if (nowRecord.first > bestRecord.first) {
            bestRecord = nowRecord
        }
        if (nowRecord.first == bestRecord.first) {
            if (nowRecord.second.size < bestRecord.second.size) {
                bestRecord = nowRecord
            }
            if (nowRecord.second.last().time!! < bestRecord.second.last().time!!) {
                bestRecord = nowRecord
            }
        }

    }
    return bestRecord
}

fun main(args: Array<String>) {
    val sc = Scanner(System.`in`)
//    sc.nextReal()
    val initNemoWeight = sc.nextReal()
    val nemoVitesseLimit = sc.nextReal()
    val timeLimit = sc.nextReal()
    val initNemoPos = Point(sc.nextReal(), sc.nextReal())


    val shrimpNum = sc.nextInt()
    val shrimps = Array<Shrimp?>(shrimpNum) { null }
    for (i in 0 until shrimpNum) {
        shrimps[i] = Shrimp(
            i + 1,
            sc.nextReal(),
            Point(sc.nextReal(), sc.nextReal()),
            Vector(sc.nextReal(), sc.nextReal())
        )
    }
    val t = System.currentTimeMillis()
    val sea = Sea(shrimps, 0.toReal(), timeLimit, initNemoPos, initNemoWeight, nemoVitesseLimit)
    val (weight, rec) = dfs(
        sea,
        Pair((-1).toReal(), arrayListOf())
    )
    println("${System.currentTimeMillis() - t}ms")

    println(rec.size)
    println(weight - initNemoWeight)
    for (r in rec) {
        println("${r.time} ${r.pos?.x} ${r.pos?.y} ${r.id}")
    }
}
