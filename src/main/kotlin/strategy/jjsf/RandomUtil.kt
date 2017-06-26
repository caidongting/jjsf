package strategy.jjsf

import java.util.*
import java.util.concurrent.ThreadLocalRandom

object RandomUtil {

    fun <T> select(values: Collection<T>, n: Int): List<T> {
        if (values.isEmpty()) {
            return emptyList()
        }
        val list = ArrayList(values)
        if (list.size <= n) {
            return list
        }
        Collections.shuffle(list)
        return list.subList(0, n)
    }

    fun between(min: Int, max: Int): Int {
        return between(min.toLong(), max.toLong()).toInt()
    }

    fun between(min: Long, max: Long): Long {
        require(max >= min) { "max=$max must not less than min=$min" }
        if (max - min > 0) {
            return ThreadLocalRandom.current().nextLong(max - min + 1) + min
        } else {
            return min
        }
    }
    fun between(min: Int, max: Int, n: Int): List<Int> {
        require(max >= min) { "max=$max must not less than min=$min" }
        return select((min..max).distinct(), n)
    }
}