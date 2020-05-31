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
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

@SupportedAnnotationTypes("in.abaddon.ladon.*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(LadonProcessor.OPTION_STRICT)
class LadonProcessor : AbstractProcessor(){
    companion object{
        const val OPTION_STRICT = "strict"
    }

    val constantMap = mutableMapOf<Pair<String, String>, Any>()
    val memberVarElements: MutableMap<Pair<String, String>, Guard> = mutableMapOf()

    //                             [(class, method, [type]), [predicate]]
    val methodElements: MutableMap<Triple<String, String, List<String>>, List<Guard?>> = mutableMapOf()
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

        roundEnv
            .getElementsAnnotatedWith(Positive::class.java)
            .filter { it.enclosingElement is TypeElement }
            .forEach {
                val classElement = it.enclosingElement as TypeElement
                val varClassName = Pair(it.simpleName.toString(), classElement.qualifiedName.toString())
                memberVarElements.put(varClassName, PositiveGuard)
            }

        roundEnv
            .rootElements.flatMap { it.enclosedElements }
            .filterIsInstance<ExecutableElement>()
            .forEach {
                val classElement = it.enclosingElement as TypeElement
                val methodName = it.simpleName.toString()
                val typeList = it.parameters.map{ it.asType().toString() }

                val methodSignature = Triple(classElement.qualifiedName.toString(), methodName, typeList)
                val guards = it.parameters.map{ mapAnnotationsToGuard(it.annotationMirrors) }

                warn("___ $methodSignature")
                methodElements.put(methodSignature, guards)
            }

        // TODO if set, use strict handling!
        warn(">arg  ${processingEnv.getOptions().get(OPTION_STRICT)}")

        return true
    }

    private fun mapAnnotationsToGuard(annotations: List<AnnotationMirror>): Guard? {
        // TODO enable combine Predicates
        if(annotations.isEmpty()) return null

        return annotations.map {
            when (it.annotationType.toString()) {
                Positive::class.qualifiedName -> PositiveGuard
                else -> null
            }
        }
        .first { it is Guard }
    }

    private fun warn(msg: String) = messager.printMessage(Diagnostic.Kind.WARNING, msg)
    private fun error(msg: String) = messager.printMessage(Diagnostic.Kind.ERROR, msg)

    inner class LadonTaskListener(): TaskListener{
        val typeCheckingScanner = TypeCheckingScanner(memberVarElements, methodElements, messager, constantMap)

        override fun finished(event: TaskEvent) {
            if(event.kind == TaskEvent.Kind.ANALYZE){
                val treePath = Trees.instance(processingEnv).getPath(event.typeElement)
                typeCheckingScanner.scan(treePath, TraversalBag(null, false));
            }

        }

        override fun started(event: TaskEvent) {}
    }
}
