package `in`.abaddon.ladon

import com.sun.source.tree.AssignmentTree
import com.sun.source.tree.BlockTree
import com.sun.source.tree.ClassTree
import com.sun.source.tree.ExpressionTree
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

data class TraversalBag(
    val qualifiedNameOfEnclosingClass: String?,
    val fromAssignment: Boolean
)

class TypeCheckingScanner(
    val elements: MutableMap<Pair<String, String>, Guard>,
    val messager: Messager,
    val constantMap: MutableMap<Pair<String, String>, Any>
): TreePathScanner<Any?, TraversalBag>(){
    //                        [variableName, value]
    val localVariableMap: MutableMap<String, Any> = mutableMapOf()

    protected fun warn(msg: String) = messager.printMessage(Diagnostic.Kind.WARNING, msg)
    protected fun error(msg: String) = messager.printMessage(Diagnostic.Kind.ERROR, msg)

    override fun visitBlock(node: BlockTree, bag: TraversalBag): Any? {
        localVariableMap.clear()
        return super.visitBlock(node, bag)
    }

    override fun visitVariable(node: VariableTree, bag: TraversalBag): Any? {
        val rhsValue = scan(node.initializer, bag)

        if(rhsValue != null) {
            localVariableMap.put(node.name.toString(), rhsValue)
        }

        return super.visitVariable(node, bag)
    }

    private fun collectLocalVariable(identNode: IdentifierTree, rhsNode: ExpressionTree, bag: TraversalBag){
        val varName = identNode.name.toString()
        val rhsValue = scan(rhsNode, bag)

        if(localVariableMap.containsKey(varName) && rhsValue != null){
            localVariableMap.put(varName, rhsValue)
        }
    }

    private fun verifyAssignment(varClassPair: Pair<String, String?>, node: AssignmentTree, bag: TraversalBag){
        val rhsValue = scan(node.expression, bag.copy(fromAssignment = true))
        val predicate = elements[varClassPair]

        if(predicate == null || rhsValue == null) return

        if(!predicate.isValid(rhsValue)) {
            error("$node ${predicate.msg}")
        }
    }

    override fun visitAssignment(node: AssignmentTree, bag: TraversalBag): Any? {

        val lhs = node.variable
        val rhs = node.expression

        warn("---------------------")
        warn("Assignment node $node")
        warn("Assignment lhs $lhs ${lhs.javaClass.simpleName}")
        warn("Assignment rhs $rhs ${rhs.javaClass.simpleName}")

        if(lhs is JCTree.JCIdent){
            collectLocalVariable(lhs, rhs, bag)
        }

        if(lhs is JCTree.JCFieldAccess){
            val className = lhs.expression.type.toString()
            val varName = lhs.identifier.toString()
            val pair = Pair(varName,className)

            verifyAssignment(pair, node, bag)
        }

        if(lhs is JCTree.JCIdent && !localVariableMap.containsKey(lhs.name.toString())){
            val varName = lhs.name.toString()
            val pair = Pair(varName, bag.qualifiedNameOfEnclosingClass)
            verifyAssignment(pair, node, bag)
        }

        return super.visitAssignment(node, bag)
    }

    override fun visitMemberSelect(node: MemberSelectTree, bag: TraversalBag): Any? {
        if(node is JCTree.JCFieldAccess) {
            val varClassPair = Pair(node.name.toString(), node.selected.type.toString())
            val staticValue = constantMap[varClassPair]

            if(staticValue != null) return staticValue
        }

        return super.visitMemberSelect(node, bag)
    }

    override fun visitIdentifier(node: IdentifierTree, bag: TraversalBag): Any? {
        val varName = node.name.toString();
        val varClassPair = Pair(varName, bag.qualifiedNameOfEnclosingClass)

        if(bag.fromAssignment && localVariableMap.containsKey(varName)) {
            return localVariableMap[varName]
        }

        if(bag.fromAssignment && constantMap.containsKey(varClassPair)){
            return constantMap[varClassPair]
        }

        return super.visitIdentifier(node, bag)
    }

    override fun visitClass(node: ClassTree, bag: TraversalBag): Any? {
        val classDecl = node as JCTree.JCClassDecl
        return super.visitClass(node, bag.copy(qualifiedNameOfEnclosingClass = classDecl.type.toString()))
    }

    override fun visitLiteral(node: LiteralTree, bag: TraversalBag): Any? {
        return node.value
    }

    override fun visitUnary(node: UnaryTree, bag: TraversalBag): Any? {
        val unary = node as JCTree.JCUnary
        val expr = scan(unary.expression,bag)

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
