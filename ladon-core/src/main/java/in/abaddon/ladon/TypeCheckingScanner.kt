package `in`.abaddon.ladon

import com.sun.source.tree.AssignmentTree
import com.sun.source.tree.BlockTree
import com.sun.source.tree.ClassTree
import com.sun.source.tree.IdentifierTree
import com.sun.source.tree.LiteralTree
import com.sun.source.tree.MemberSelectTree
import com.sun.source.tree.MethodInvocationTree
import com.sun.source.tree.UnaryTree
import com.sun.source.tree.VariableTree
import com.sun.source.util.TreePathScanner
import com.sun.tools.javac.code.Type
import com.sun.tools.javac.tree.JCTree
import java.lang.AssertionError
import javax.annotation.processing.Messager
import javax.lang.model.element.Modifier
import javax.tools.Diagnostic

object NULL_LITERAL

data class TraversalBag(
    val qualifiedNameOfEnclosingClass: String?,
    val fromAssignment: Boolean
)

class TypeCheckingScanner(
    val memberVarElements: MutableMap<Pair<String, String>, Guard>,
    val methodElements: MutableMap<Triple<String, String, List<String>>, List<Guard?>>,
    val messager: Messager,
    val constantMap: MutableMap<Pair<String, String>, Any>
): TreePathScanner<Any?, TraversalBag>(){
    //                        [variableName, value]
    val localConstantMap: MutableMap<String, Any> = mutableMapOf()
    val localVariableSet: MutableSet<String> = mutableSetOf()

    // insertion order = [Derived, Base]
    val superTypes: MutableList<String> = mutableListOf()
    val interfaceTypes: MutableList<Type> = mutableListOf()

    protected fun warn(msg: String) = messager.printMessage(Diagnostic.Kind.WARNING, msg)
    protected fun error(msg: String) = messager.printMessage(Diagnostic.Kind.ERROR, msg)

    override fun visitBlock(node: BlockTree, bag: TraversalBag): Any? {
        localConstantMap.clear()
        localVariableSet.clear()
        return super.visitBlock(node, bag)
    }

    override fun visitMethodInvocation(node: MethodInvocationTree, bag: TraversalBag): Any? {
        val methodSelect = node.methodSelect
        if(methodSelect is IdentifierTree){
            val methodName = methodSelect.name.toString()
            val typeList = node.arguments.filterIsInstance<JCTree.JCExpression>().map { it.type.toString() }
            val methodSignature = Triple(bag.qualifiedNameOfEnclosingClass!!, methodName, typeList)


            val n = node as JCTree.JCMethodInvocation
            warn(">>>> ${node.meth.javaClass.simpleName}")

            if(methodElements.containsKey(methodSignature)){
                val guards = methodElements[methodSignature]!!

                warn(">>>> INSIDE")

                node.arguments.forEachIndexed { idx, expr -> run {
                    val resolvedValue = scan(expr, bag)
                    val predicate = guards[idx]
                    if(resolvedValue != null && predicate != null && !predicate.isValid(resolvedValue)){
                        error("$node ${predicate.msg}")
                    }
                } }
            }
        }

        return super.visitMethodInvocation(node, bag)
    }

    override fun visitVariable(node: VariableTree, bag: TraversalBag): Any? {
        val isFinal = node.modifiers.flags.any { it == Modifier.FINAL }
        val rhsValue = scan(node.initializer, bag)

        val varName = node.name.toString()
        localVariableSet.add(varName)

        if(isFinal && rhsValue != null) {
            localConstantMap.put(varName, rhsValue)
        }

        return super.visitVariable(node, bag)
    }

    private fun verifyAssignment(varClassPair: Pair<String, String?>, node: AssignmentTree, bag: TraversalBag){
        val rhsValue = scan(node.expression, bag.copy(fromAssignment = true))
        val predicate = memberVarElements[varClassPair]

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

        if(lhs is JCTree.JCFieldAccess){
            val className = lhs.expression.type.toString()
            val varName = lhs.identifier.toString()
            val pair = Pair(varName,className)

            verifyAssignment(pair, node, bag)
        }

        if(lhs is JCTree.JCIdent && !localVariableSet.contains(lhs.name.toString())){
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

    private fun findFirstInterfaceType(types: List<Type>, matches:(String) -> Boolean): String?{
        if(types.isEmpty()) return null

        val maybeFirst = types.find{ matches(it.toString()) }
        if (maybeFirst != null) return maybeFirst.toString()

        val subTypes = types.filterIsInstance<Type.ClassType>()
                            .flatMap { it.interfaces_field }

        return findFirstInterfaceType(subTypes, matches)
    }

    override fun visitIdentifier(node: IdentifierTree, bag: TraversalBag): Any? {
        val varName = node.name.toString();
        val varClassPair = Pair(varName, bag.qualifiedNameOfEnclosingClass)

        if(bag.fromAssignment){
            // local scope const
            if(localConstantMap.containsKey(varName)) {
                return localConstantMap[varName]
            }

            // local scope var
            if(localVariableSet.contains(varName)) return null

            // local class const
            if(constantMap.containsKey(varClassPair)){
                return constantMap[varClassPair]
            }

            // extended const
            val firstSuperType = superTypes.find{base -> constantMap.containsKey(Pair(varName, base))}
            if(firstSuperType !=null) {
                return constantMap[Pair(varName, firstSuperType)]
            }

            // implemented const
            val firstInterface = findFirstInterfaceType(interfaceTypes){base -> constantMap.containsKey(Pair(varName, base))}
            if(firstInterface !=null) {
                return constantMap[Pair(varName, firstInterface)]
            }
        }

        return super.visitIdentifier(node, bag)
    }

    private fun collectSuperType(type: Type){
        if(type is Type.ClassType){
            superTypes.add(type.toString())
            collectSuperType(type.supertype_field)
        }
    }

    private fun collectInterfaces(types: List<Type>){
        interfaceTypes.addAll(types)
    }

    override fun visitClass(node: ClassTree, bag: TraversalBag): Any? {
        val classDecl = node as JCTree.JCClassDecl
        val type = node.type

        superTypes.clear()
        interfaceTypes.clear()

        if(type is Type.ClassType) {
            collectSuperType(type.supertype_field)
            collectInterfaces(type.interfaces_field)
        }

        return super.visitClass(node, bag.copy(qualifiedNameOfEnclosingClass = classDecl.type.toString()))
    }

    override fun visitLiteral(node: LiteralTree, bag: TraversalBag): Any? {
        if(node is JCTree.JCLiteral && node.type.toString() == "<nulltype>"){
            return NULL_LITERAL;
        }

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
