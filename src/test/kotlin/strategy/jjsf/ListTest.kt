package strategy.jjsf

import java.util.*
import kotlin.collections.HashMap

/**
 * Created by caidt on 2017/6/8.
 */
fun main(args: Array<String>) {
  val map: HashMap<Int, HashSet<Meta>> = HashMap()
//  val set: HashSet<Meta> = Sets.newHashSet()
  val meta = Meta(1, 1)
  map.add(meta)
  map.add(Meta(1, 2))
  map.add(Meta(1, 3))
  map.add(Meta(2, 4))
  map.add(Meta(3, 5))
  meta.num =10
  println(map)
  val result = map[meta.id]?.remove(meta)
  println("result=$result")
  println(map)
  mutableSetOf<Int>()
}

fun HashMap<Int, HashSet<Meta>>.add(meta: Meta) {
  this.getOrPut(meta.id) { hashSetOf() }.add(meta)
}

class JsonTest {
  val tick: Int get() = 2
  fun tick(): Long = 1
}


class Meta(val id: Int, var num: Int) {
//  override fun toString(): String {
//    return "(id=$id,num=$num)"
//  }
}

fun LinkedList<Meta>.selfMerge() {
  val iterator = this.iterator()
  var first = iterator.next()
  while (iterator.hasNext()) {
    val second = iterator.next()
    if (first.id == second.id) {
      first.num += second.num
      iterator.remove()
    } else {
      first = second
    }
  }
}