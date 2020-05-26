package `in`.abaddon.ladon

import com.sun.source.util.JavacTask
import com.sun.source.util.TaskEvent
import com.sun.source.util.TaskListener
import com.sun.source.util.Trees
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

@SupportedAnnotationTypes("in.abaddon.ladon.*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class LadonProcessor : AbstractProcessor(){
    val constantMap = mutableMapOf<Pair<String, String>, Any>()
    val elements: MutableMap<Pair<String, String>, Guard> = mutableMapOf()
    private lateinit var messager: Messager

    override @Synchronized fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        messager = processingEnv.messager

        JavacTask.instance(processingEnv).addTaskListener(LadonTaskListener())
    }

    fun collectConstants(roundEnv: RoundEnvironment){
        roundEnv.rootElements.flatMap{ typeElement ->
            typeElement.enclosedElements
                       .filter { it.modifiers.containsAll(setOf(Modifier.FINAL, Modifier.STATIC)) }
                       .filterIsInstance<VariableElement>()
                       .map { ((typeElement as TypeElement).qualifiedName) to it}
        }
        .forEach{
            val varClassPair = Pair(it.second.simpleName.toString(), it.first.toString())
            constantMap.put(varClassPair, it.second.constantValue)
        }
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        collectConstants(roundEnv)

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
        val typeCheckingScanner = TypeCheckingScanner(elements, messager, constantMap)

        override fun finished(event: TaskEvent) {
            if(event.kind == TaskEvent.Kind.ANALYZE){
                val treePath = Trees.instance(processingEnv).getPath(event.typeElement)
                typeCheckingScanner.scan(treePath, TraversalBag(null, false));
            }

        }

        override fun started(event: TaskEvent) {}
    }
}
