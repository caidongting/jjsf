package infrastructure.data

data class Point(
        val logi: Double, // 经度
        val lati: Double, // 纬度
        val postCode: Int
)

enum class TaskType {
    PickUp,
    Deliver
}

open class TaskPoint(
        private val id: Int,
        private val type: TaskType,
        private val stayTime: Int,
        private val point: Point
) : Data {
    override fun getId(): Int  = id

    override fun getType(): TaskType = type

    override fun getStayTime(): Int = stayTime

    override fun getPoint(): Point = point

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as TaskPoint

        if (id != other.id) return false
        if (point != other.point) return false
        if (type != other.type) return false
        if (stayTime != other.stayTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + point.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + stayTime
        return result
    }


}