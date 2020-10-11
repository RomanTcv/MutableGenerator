package com.r12.mutable.processor

//import com.r12.mutable.processor.entities.Entity
//import com.r12.mutable.processor.factories.EntityFactory
//import com.r12.mutable.processor.factories.FileSpecFactory
import com.google.auto.service.AutoService
import com.r12.mutable.annotations.Mutable
import com.r12.mutable.processor.extensions.printError
import com.r12.mutable.processor.factories.InterfaceGeneratorFactory
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.metadata.ImmutableKmClass
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isInterface
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

/**
 * @author R12rus
 */
@KotlinPoetMetadataPreview
@AutoService(Process::class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
class MutableProcessor : AbstractProcessor() {

    private lateinit var messager: Messager
    private lateinit var elements: Elements
    private lateinit var interfaceFactory: InterfaceGeneratorFactory

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        messager = processingEnv.messager
        elements = processingEnv.elementUtils
        interfaceFactory = InterfaceGeneratorFactory(elements)
    }

    override fun getSupportedAnnotationTypes() = setOf(Mutable::class.java.canonicalName)

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(typeElements: Set<TypeElement>, environment: RoundEnvironment): Boolean {
        environment.getElementsAnnotatedWith(Mutable::class.java)
            .mapNotNull(::generateFileSpec)
            .forEach {
                it.writeTo(processingEnv.filer)
            }
        return true
    }

    private fun generateFileSpec(element: Element): FileSpec? {
        if (element !is TypeElement) {
            messager.printError("@Mutable can't be applied to $element. Must be a Kotlin interface")
            return null
        }

        val kmClass = element.toImmutableKmClass()
        if (!isSuitable(kmClass)) return null

        return interfaceFactory.generate(element, kmClass)
    }

    private fun isSuitable(kmClass: ImmutableKmClass): Boolean {
        if (kmClass.isInterface) return true

        messager.printError("${kmClass.name} must be kotlin interface")
        return false
    }
}