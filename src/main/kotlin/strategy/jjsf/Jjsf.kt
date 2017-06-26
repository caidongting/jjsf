package strategy.jjsf

import com.google.common.math.DoubleMath
import infrastructure.Plan
import infrastructure.data.Data
import infrastructure.data.DataPool
import infrastructure.data.Point
import java.math.RoundingMode
import java.util.*

/**
 * Created by caidt on 2016/11/29.
 */
enum class PARAMS(val property: String, val defaultValue: String) {
    TABLE_SIZE("jjsf.table.size", "10"),
    RUN_STEPS("jjsf.run.steps", "1000"),
    SPEED("jjsf.project.speed", "40.0"),
    TIME("jjsf.project.time", "200"),
    ADD_TIME("jjsf.project.addTime", "40")
    ;
}

fun Properties.fetch(param: PARAMS): String {
    return this.getProperty(param.property, param.defaultValue)
}

class ForbiddenTable<in T : Data>(val size: Int) {
    private val list: MutableList<T> = mutableListOf()
    private var point: Int = 0

    fun forbid(data: T) {
        list.add(point, data)
        point = (point + 1) % size
    }

    fun isForbidden(data: T): Boolean = data in list
}

const val R = 6371393  // (m) 地球平均半径

object Calculator {

    fun p2pDistance(p1: Point, p2: Point): Double {

        val r = Math.acos(Math.sin(p1.lati * Math.PI / 180 ) * Math.sin(p2.lati * Math.PI / 180) +
                Math.cos(p1.lati * Math.PI / 180) * Math.cos(p2.lati * Math.PI / 180) * Math.cos((p1.logi - p2.logi) * Math.PI / 180))
        return R * r
    }

    fun p2pTime(p1: Point, p2: Point, speed: Double): Int {
        val distance = p2pDistance(p1, p2)
        return DoubleMath.roundToInt(distance / speed, RoundingMode.FLOOR)
    }
}

object Judge {

    fun isAllPointInclude(plan: Plan, dataPool: DataPool): Boolean {
        return plan.plan.flatMap { it.path }.containsAll(dataPool.findAll())
    }

    fun isPathFull(path: Plan.Path, minutes: Int): Boolean {
        return path.isFull
    }

    fun isStartPoint(data: Data): Boolean {
        return data.id == 0
    }
}

