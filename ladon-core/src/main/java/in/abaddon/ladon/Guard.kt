package `in`.abaddon.ladon

interface Guard {
    val msg: String
    fun isValid(expr: Any): Boolean
}
