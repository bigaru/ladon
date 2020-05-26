package `in`.abaddon.ladon

import com.sun.source.tree.AssignmentTree
import com.sun.source.tree.BlockTree
import com.sun.source.tree.ClassTree
import com.sun.source.tree.IdentifierTree
import com.sun.source.tree.LiteralTree
import com.sun.source.tree.MemberSelectTree
import com.sun.source.tree.UnaryTree
import com.sun.source.tree.VariableTree
import com.sun.source.util.TreePathScanner
import com.sun.tools.javac.tree.JCTree
import java.lang.AssertionError
import javax.annotation.processing.Messager
import javax.tools.Diagnostic

data class MetaObject(
    val qualifiedNameOfEnclosingClass: String?,
    //                        [variableName, value]
    val localVariableMap: MutableMap<String, Any>,
    val fromAssignment: Boolean
)

class TypeCheckingScanner(
    val elements: MutableMap<Pair<String, String>, Guard>,
    val messager: Messager,
    val constantMap: MutableMap<Pair<String, String>, Any>
): TreePathScanner<Any?, MetaObject>(){
    protected fun warn(msg: String) = messager.printMessage(Diagnostic.Kind.WARNING, msg)
    protected fun error(msg: String) = messager.printMessage(Diagnostic.Kind.ERROR, msg)

    override fun visitBlock(node: BlockTree, p: MetaObject): Any? {
        p.localVariableMap.clear()
        return super.visitBlock(node, p)
    }

    override fun visitVariable(node: VariableTree, p: MetaObject): Any? {
        val rhsValue = scan(node.initializer, p)

        if(rhsValue != null) {
            p.localVariableMap.put(node.name.toString(), rhsValue)
        }

        return super.visitVariable(node, p)
    }

    override fun visitAssignment(node: AssignmentTree, p: MetaObject): Any? {

        val lhs = node.variable
        val rhs = node.expression

        warn("--------------------- ${constantMap.size}")
        warn("Assignment node $node")
        warn("Assignment lhs $lhs ${lhs.javaClass.simpleName}")
        warn("Assignment rhs $rhs ${rhs.javaClass.simpleName}")

        if(lhs is JCTree.JCIdent){
            val varName = lhs.name.toString()
            val rhsValue = scan(node.expression, p)

            if(p.localVariableMap.containsKey(varName) && rhsValue != null){
                p.localVariableMap.put(varName, rhsValue)
            }
        }

        if(lhs is JCTree.JCFieldAccess && !(rhs is JCTree.JCMethodInvocation)){
            val rhsValue = scan(rhs,p.copy(fromAssignment = true))

            val className = lhs.expression.type.toString()
            val varName = lhs.identifier.toString()
            val pair = Pair(varName,className)

            val guard = elements[pair]
            if(guard == null || rhsValue == null) return super.visitAssignment(node, p)

            if(!guard.isValid(rhsValue)) {
                error("$node ${guard.msg}")
            }
        }

        if(lhs is JCTree.JCIdent && !(rhs is JCTree.JCMethodInvocation)){
            val rhsValue = scan(rhs,p.copy(fromAssignment = true))

            val varName = lhs.name.toString()
            val pair = Pair(varName, p.qualifiedNameOfEnclosingClass)

            val guard = elements[pair]
            if(guard == null || rhsValue == null) return super.visitAssignment(node, p)

            if(!guard.isValid(rhsValue)) {
                error("$node ${guard.msg}")
            }
        }

        return super.visitAssignment(node, p)
    }

    override fun visitMemberSelect(node: MemberSelectTree, p: MetaObject): Any? {
        if(node is JCTree.JCFieldAccess) {
            val varClassPair = Pair(node.name.toString(), node.selected.type.toString())
            val staticValue = constantMap[varClassPair]

            if(staticValue != null) return staticValue
        }

        return super.visitMemberSelect(node, p)
    }

    override fun visitIdentifier(node: IdentifierTree, p: MetaObject): Any? {
        if(p.fromAssignment) {
            return p.localVariableMap[node.name.toString()]
        }
        return super.visitIdentifier(node, p)
    }

    override fun visitClass(node: ClassTree, p: MetaObject): Any? {
        val classDecl = node as JCTree.JCClassDecl
        return super.visitClass(node, p.copy(qualifiedNameOfEnclosingClass = classDecl.type.toString()))
    }

    override fun visitLiteral(node: LiteralTree, p: MetaObject): Any? {
        return node.value
    }

    override fun visitUnary(node: UnaryTree, p: MetaObject): Any? {
        val unary = node as JCTree.JCUnary
        val expr = scan(unary.expression,p)

        if(expr != null && unary.tag == JCTree.Tag.NEG) {
            return when (expr) {
                is Byte -> -expr
                is Short -> -expr
                is Int -> -expr
                is Long -> -expr
                is Float -> -expr
                is Double -> -expr
                else -> throw AssertionError("should have handled all cases")
            }
        }

        return expr
    }
}
