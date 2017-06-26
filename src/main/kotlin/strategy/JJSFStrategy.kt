package strategy

import infrastructure.Plan
import infrastructure.Strategy
import infrastructure.data.Data
import infrastructure.data.DataPool
import strategy.jjsf.*
import java.util.*

/**
 * Created by caidt on 2016/11/29.
 */
class JJSFStrategy : Strategy() {

  lateinit private var forbiddenTable: ForbiddenTable<Data>

  private val timeMap = HashMap<Pair<Int, Int>, Int>()

  lateinit private var startPoint: Data

  private var plan: Plan? = null

  override fun init(dataPool: DataPool) {
    status = Status.INITIALISING
    startPoint = dataPool.findAll().first()

    val tableSize = dynamicProperties.fetch(PARAMS.TABLE_SIZE).toInt()
    forbiddenTable = ForbiddenTable(tableSize)

    initTimeMap(dataPool)

    val minutes = dynamicProperties.fetch(PARAMS.TIME).toInt()
    plan = initSolution(dataPool, minutes)
  }

  private fun initTimeMap(dataPool: DataPool) {
    val speed = dynamicProperties.fetch(PARAMS.SPEED).toDouble() * 1000 / 60 // m/min
    val all = dataPool.findAll()
    all.forEach { datax ->
      all.filter { it.id > datax.id }.forEach { datay ->
        val costMinute = Calculator.p2pTime(datax.point, datay.point, speed)
        timeMap.put(datax.id to datay.id, costMinute)
      }
    }
  }

  private fun initSolution(dataPool: DataPool, minutes: Int): Plan {
    val initSolution = Plan()
    var path = createPath(minutes)
    initSolution.plan.add(path)
    dataPool.findAll().filter { it.id > 0 }.forEach { data ->
      while (!addToPath(path, data, minutes)) {
        assert(isValid(path)) { "path is invalid!" }
        assert(Judge.isPathFull(path, minutes)) { "error, path is not full" }
        path = createPath(minutes)
        initSolution.plan.add(path)
      }
    }
    assert(Judge.isAllPointInclude(initSolution, dataPool)) { "error, not all point included" }
    return initSolution
  }

  private fun p2pTime(data1: Data, data2: Data): Int {
    return when {
      data1 == data2 -> 0
      data1.id > data2.id -> timeMap[data2.id to data1.id]!!
      else -> timeMap[data1.id to data2.id]!!
    }
  }

  private fun createPath(minutes: Int): Plan.Path {
    val path = Plan.Path()
    addToPath(path, startPoint, minutes)
    return path
  }

  private fun addToPath(path: Plan.Path, data: Data, minutes: Int): Boolean {
    if (path.path.isEmpty()) {
      assert(data.stayTime <= minutes) { "start point is illegal " }
      path.path.add(data)
      path.totalMinutes = data.stayTime
      return true
    } else {
      val costMinute = p2pTime(path.path.last(), data) + data.stayTime
      if (path.totalMinutes + costMinute + p2pTime(data, path.path.first()) <= minutes) {
        path.totalMinutes += costMinute
        path.path.add(data)
        return true
      } else {
        path.isFull = true
        path.totalMinutes += p2pTime(path.path.last(), path.path.first())
        return false
      }
    }
  }

  private fun isValid(path: Plan.Path): Boolean {
    if (path.path.isEmpty()) return false
    var totalMinute = 0
    val iterator = path.path.iterator()
    var next = iterator.next()
    while (iterator.hasNext()) {
      val curr = next
      next = iterator.next()
      totalMinute += curr.stayTime + p2pTime(curr, next)
    }
    totalMinute += next.stayTime + p2pTime(next, path.path.first())
    return totalMinute == path.totalMinutes
  }

  override fun run() {
    status = Status.RUNNING
    val steps = dynamicProperties.fetch(PARAMS.RUN_STEPS).toInt()
    val minutes = dynamicProperties.fetch(PARAMS.TIME).toInt()
    run0(minutes, steps)
    status = Status.STOPPED
  }

  private fun run0(time: Int, steps: Int) {
    var rounds = 0
    try {
      do {
        runOnce(time)
        selfClean()
      } while (rounds++ < steps)
    } catch(e: Exception) {
      status = Status.ERROR
      throw RuntimeException("禁忌算法运行出错", e)
    }
  }

  /**
   * 目标：生成候补解，找出暂时最优解，放入禁忌表，然后进行交换
   * 同时统计那个解出现的次数
   */
  private fun runOnce(time: Int) {
    // generate 生成候补解
    val nextGroup = generate()
    // todo:judge 判断局部最优解，需与禁忌表联合使用
    val next = judge(nextGroup, time)
    // exchange
    exchange(next)
    // collect
  }

  data class ActionIndex(val pathIndex: Int, val pointIndex: Int)

  data class Action(val from: ActionIndex, val to: ActionIndex)

  private fun generate(): List<Action> {
    fun select(plan: List<Plan.Path>): ActionIndex {
      val list = RandomUtil.select(plan, 1)
      val current = list[0]
      check(current.path.size >= 2)
      val exchange = RandomUtil.between(1, current.path.size - 1, 1) // 0 是startPoint
      val index = plan.indexOf(current)
      return ActionIndex(index, exchange[0])
    }

    val next = arrayListOf<Action>()
    repeat(6) {
      val plan = plan!!.plan
      val a1 = select(plan)
      val a2 = select(plan)
      next.add(Action(a1, a2))
    }
    return next
  }

  private fun judge(nextGroup: List<Action>, time: Int): Action {
    return nextGroup[0]
  }

  private fun exchange(action: Action) {
    val (from, dest) = action
    try {
      if (from.pathIndex == dest.pathIndex) {
        val path = plan!!.plan[from.pathIndex]
        Collections.swap(path.path, from.pointIndex, dest.pointIndex)
      } else {
        val path1 = plan!!.plan[from.pathIndex]
        val path2 = plan!!.plan[dest.pathIndex]
        // to be completed
      }
    } catch(e: Exception) {
      e.printStackTrace()
    }
  }

  private fun selfClean() {
    plan?.apply {
      plan = plan.filter { it.path.size > 1 }
    }
  }

  fun stop() {
    status = Status.STOPPING
    // todo: 处理关闭前要处理的东西
    status = Status.STOPPED
  }

  override fun collectResult(): Plan? {
    return plan
  }
}