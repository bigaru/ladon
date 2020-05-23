package `in`.abaddon.ladon

import java.lang.AssertionError
import javax.lang.model.element.Element

//enum class TYPE{
//    BOOLEAN,
//
//    CHAR,
//
//    BYTE,
//    SHORT,
//    INT,
//    LONG,
//
//    FLOAT,
//    DOUBLE,
//
//    STRING,
//}
//
//object Utils{
//    fun getType(str: String): TYPE{
//        return when(str){
//            "byte"-> TYPE.BYTE
//            "java.lang.Byte" -> TYPE.BYTE
//
//            "short"-> TYPE.SHORT
//            "java.lang.Short" -> TYPE.SHORT
//
//            "int"-> TYPE.INT
//            "java.lang.Integer" -> TYPE.INT
//
//            "long"-> TYPE.LONG
//            "java.lang.Long" -> TYPE.LONG
//
//            "float"-> TYPE.FLOAT
//            "java.lang.Float" -> TYPE.FLOAT
//
//            "double"-> TYPE.DOUBLE
//            "java.lang.Double" -> TYPE.DOUBLE
//
//            else -> throw AssertionError("forgot to implement")
//        }
//    }
//}
//
//fun Element.getTYPE() = Utils.getType(this.asType().toString())

interface Guard {
    val msg: String
    fun isValid(expr: Any): Boolean
}
