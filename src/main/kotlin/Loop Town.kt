package org.example

class Road{
    constructor()
    constructor(next: Road?, building: Building?) {
        this.next = next
        this.building = building
    }
    var next: Road? = null
    var building: Building? = null

    companion object var count = 0

    val hash = count++
    override fun hashCode(): Int {
        return hash
    }
}

interface Building

class House: Building
class Office: Building