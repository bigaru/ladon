package `in`.abaddon.ladon

import com.sun.source.tree.AssignmentTree
import com.sun.source.tree.BlockTree
import com.sun.source.tree.ClassTree
import com.sun.source.tree.IdentifierTree
import com.sun.source.tree.LiteralTree
import com.sun.source.tree.UnaryTree
import com.sun.source.tree.VariableTree
import com.sun.source.util.JavacTask
import com.sun.source.util.TaskEvent
import com.sun.source.util.TaskListener
import com.sun.source.util.TreePathScanner
import com.sun.source.util.Trees
import com.sun.tools.javac.tree.JCTree
import java.lang.AssertionError
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

data class MetaObject(
    val qNameOfEnclosingClass: String?,
                      // [variableName, value]
    val localVariableMap: MutableMap<String, Any>,
    val fromAssignment: Boolean
)

@SupportedAnnotationTypes("in.abaddon.ladon.*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class LadonProcessor : AbstractProcessor(){

    val elements: MutableMap<Pair<String, String>, Guard> = mutableMapOf()
    private lateinit var messager: Messager

    override @Synchronized fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        messager = processingEnv.messager

        JavacTask.instance(processingEnv).addTaskListener(LadonTaskListener())
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {

        roundEnv.getElementsAnnotatedWith(Positive::class.java).forEach {
            val classElement = it.enclosingElement as TypeElement
            val pair = Pair(it.simpleName.toString(), classElement.qualifiedName.toString())
            elements.put(pair, PositiveGuard)
        }

        return true
    }

    private fun warn(msg: String) = messager.printMessage(Diagnostic.Kind.WARNING, msg)
    private fun error(msg: String) = messager.printMessage(Diagnostic.Kind.ERROR, msg)

    inner class LadonTaskListener(): TaskListener{
        override fun finished(event: TaskEvent) {

            if(event.kind == TaskEvent.Kind.ANALYZE){
                val treePath = Trees.instance(processingEnv).getPath(event.typeElement)
                TreeScanner().scan(treePath, MetaObject(null, mutableMapOf(), false));
            }

        }

        override fun started(event: TaskEvent) {}
    }

    inner class TreeScanner(): TreePathScanner<Any?, MetaObject>(){
        override fun visitBlock(node: BlockTree, p: MetaObject): Any? {
            p.localVariableMap.clear()
            return super.visitBlock(node, p)
        }

        override fun visitVariable(node: VariableTree, p: MetaObject): Any? {
            val rhsValue = scan(node.initializer, p)
            if(rhsValue != null) p.localVariableMap.put(node.name.toString(), rhsValue)
            return super.visitVariable(node, p)
        }

        override fun visitAssignment(node: AssignmentTree, p: MetaObject): Any? {

            val lhs = node.variable
            val rhs = node.expression

            warn("---------------------")
            warn("Assignment node $node")
            warn("Assignment lhs $lhs ${lhs.javaClass.simpleName}")
            warn("Assignment rhs $rhs ${rhs.javaClass.simpleName}")

            if(lhs is JCTree.JCFieldAccess && !(rhs is JCTree.JCMethodInvocation)){
                val rhsValue = scan(rhs,p.copy(fromAssignment = true))

                val className = lhs.expression.type.toString()
                val varName = lhs.identifier.toString()
                val pair = Pair(varName,className)

                val guard = elements[pair]
                if(guard == null || rhsValue == null) return Unit

                if(!guard.isValid(rhsValue)) {
                    error("$node ${guard.msg}")
                }
            }

            if(lhs is JCTree.JCIdent && !(rhs is JCTree.JCMethodInvocation)){
                val rhsValue = scan(rhs,p.copy(fromAssignment = true))

                val varName = lhs.name.toString()
                val pair = Pair(varName, p.qNameOfEnclosingClass)

                val guard = elements[pair]
                if(guard == null || rhsValue == null) return Unit

                if(!guard.isValid(rhsValue)) {
                    error("$node ${guard.msg}")
                }
            }

            return super.visitAssignment(node, p)
        }

        override fun visitIdentifier(node: IdentifierTree, p: MetaObject): Any? {
            if(p.fromAssignment) {
                return p.localVariableMap[node.name.toString()]
            }
            return super.visitIdentifier(node, p)
        }

        override fun visitClass(node: ClassTree, p: MetaObject): Any? {
            val classDecl = node as JCTree.JCClassDecl
            return super.visitClass(node, p.copy(qNameOfEnclosingClass = classDecl.type.toString()))
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
}

