package `in`.abaddon.ladon

import com.sun.source.tree.AssignmentTree
import com.sun.source.tree.ExpressionTree
import com.sun.source.util.JavacTask
import com.sun.source.util.TaskEvent
import com.sun.source.util.TaskListener
import com.sun.source.util.TreePathScanner
import com.sun.source.util.Trees
import com.sun.tools.javac.tree.JCTree
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic


data class NumberCondition(val msg: (ExpressionTree) -> String, val isValid: (Int) -> Boolean)

@SupportedAnnotationTypes("in.abaddon.ladon.*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class LadonProcessor : AbstractProcessor(){

    val elements: MutableMap<Pair<String, String>, NumberCondition> = mutableMapOf()
    private lateinit var messager: Messager

    override @Synchronized fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        messager = processingEnv.messager

        JavacTask.instance(processingEnv).addTaskListener(LadonTaskListener())
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {

        roundEnv.getElementsAnnotatedWith(Positive::class.java)
                .forEach {
            val classElement = it.enclosingElement as TypeElement
            val pair = Pair(it.simpleName.toString(), classElement.qualifiedName.toString())

            val msg = {expr: ExpressionTree -> "${expr} must be positive"}
            val condition = {n: Int -> n > 0}

            elements.put(pair, NumberCondition(msg, condition))
        }

        return true
    }

    private fun warn(msg: String) = messager.printMessage(Diagnostic.Kind.WARNING, msg)
    private fun error(msg: String) = messager.printMessage(Diagnostic.Kind.ERROR, msg)

    inner class LadonTaskListener(): TaskListener{
        override fun finished(event: TaskEvent) {

            if(event.kind == TaskEvent.Kind.ANALYZE){
                val treePath = Trees.instance(processingEnv).getPath(event.typeElement)
                TreeScanner().scan(treePath, null);
            }

        }

        override fun started(event: TaskEvent) {}
    }

    inner class TreeScanner(): TreePathScanner<Unit, Any>(){
        override fun visitAssignment(node: AssignmentTree, p: Any?) {
            super.visitAssignment(node, p)

            val lhs = node.variable
            val rhs = node.expression

            warn("---------------------")
            warn("Assignment node $node")
            warn("Assignment lhs $lhs ${lhs.javaClass.simpleName}")
            warn("Assignment rhs $rhs ${rhs.javaClass.simpleName}")

            if(lhs is JCTree.JCFieldAccess && rhs is JCTree.JCLiteral){
                val className = lhs.expression.type.toString()
                val varName = lhs.identifier.toString()
                val pair = Pair(varName,className)

                val condition = elements[pair]
                if(condition == null) return

                if(!condition.isValid(rhs.value as Int)) {
                    error(condition.msg(node))
                }
            }
        }
    }

    /*
     * foo.no           = 3
     * this.no          = 3
     * JCFieldAccess    = JCLiteral
     *
     * foo.no
     * Expression.Identifier
     */
}

