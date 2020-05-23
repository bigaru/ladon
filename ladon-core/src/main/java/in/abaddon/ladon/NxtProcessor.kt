package `in`.abaddon.ladon

import com.sun.source.tree.AssignmentTree
import com.sun.source.util.*
import com.sun.tools.javac.tree.JCTree
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Name
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@SupportedAnnotationTypes("in.abaddon.jrefined.core.Guard")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class NxtProcessor : AbstractProcessor(){

    var elements: MutableList<Pair<Name, Name>> = mutableListOf()

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)

        JavacTask.instance(processingEnv).addTaskListener(NxtTaskListener(processingEnv.messager))

    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {

        val newElements = roundEnv.getElementsAnnotatedWith(Guard::class.java).toList()

        newElements.forEach {
            val classElement = it.enclosingElement as TypeElement
            val pair = Pair(it.simpleName, classElement.qualifiedName)
            processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, "Proc" + pair)

            elements.add(pair)
        }

        return true
    }


    inner class NxtTaskListener(val messager: Messager): TaskListener{
        override fun finished(event: TaskEvent) {
            //messager.printMessage(Diagnostic.Kind.WARNING, "- " + event.kind)

            if(event.kind == TaskEvent.Kind.ANALYZE){
                messager.printMessage(Diagnostic.Kind.WARNING, "Meow")

                val p = Trees.instance(processingEnv).getPath(event.typeElement)
                val scanner = TreeScanner(messager);
                scanner.scan(p, null);
            }
        }

        override fun started(event: TaskEvent) {}
    }

    inner class TreeScanner(val messager: Messager): TreePathScanner<Unit, Any>(){
        override fun visitAssignment(node: AssignmentTree, p: Any?) {
            super.visitAssignment(node, p)

            val rhs = node.expression as JCTree.JCLiteral
            val lhs = node.variable as JCTree.JCFieldAccess

            val selected = lhs.selected as JCTree.JCIdent

            messager.printMessage(Diagnostic.Kind.WARNING, "Assignment Expr " + node.expression)
            messager.printMessage(Diagnostic.Kind.WARNING, "Assignment Type " + rhs.type)

            messager.printMessage(Diagnostic.Kind.WARNING, "Assignment name " + lhs.name)
            messager.printMessage(Diagnostic.Kind.WARNING, "Assignment " + selected.type)

            elements.forEach {
                val matchesVariable = it.first.toString() == lhs.name.toString()
                val matchesClass = it.second.toString() == selected.type.toString()
                val value = rhs.value as Int

                if(matchesClass && matchesVariable && value < 0){
                    messager.printMessage(Diagnostic.Kind.ERROR, "${node} must not be negative")
                }
            }
        }
    }
}

