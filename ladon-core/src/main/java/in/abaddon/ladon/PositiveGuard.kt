package `in`.abaddon.ladon

import java.lang.AssertionError

object PositiveGuard: Guard {
    override val msg = "must be positive"
    override fun isValid(expr: Any): Boolean {
        return when(expr){
            is Byte -> expr > 0
            is Short -> expr > 0
            is Int -> expr > 0
            is Long -> expr > 0
            is Float -> expr > 0
            is Double -> expr > 0
            else -> throw AssertionError("should have handled all cases -> ${expr.javaClass.simpleName}")
        }
    }
}
