package com.r12.mutable.processor.factories

import com.r12.mutable.processor.entities.Field
import com.r12.mutable.processor.entities.Method
import com.r12.mutable.processor.extensions.asTypeElement
import com.r12.mutable.processor.extensions.formatType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.metadata.ImmutableKmClass
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import kotlinx.metadata.KmClassifier
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements

@KotlinPoetMetadataPreview
internal open class BaseFactory(protected val elements: Elements) {

    protected fun TypeElement.getFields(): Collection<Field> {
        val properties: MutableSet<Field> = try {
            val kmClass: ImmutableKmClass =
                toImmutableKmClass()
            kmClass.properties
                .filter { it.returnType.classifier is KmClassifier.Class }
                .map(::Field)
                .toMutableSet()
        } catch (exc: IllegalStateException) {
            // Throws IllegalStateException for java classes
            mutableSetOf()
        }

        properties += getSuperProperties {
            it.getFields()
        }

        return properties
    }

    protected fun TypeElement.getMethods(): Collection<Method> {
        val methods: MutableSet<Method> = try {
            toImmutableKmClass().functions
                .map(::Method)
                .toMutableSet()
        } catch (exc: IllegalStateException) {
            enclosedElements.mapNotNull {
                if (it.kind == ElementKind.METHOD && !it.modifiers.contains(Modifier.STATIC)) {
                    Method(it as ExecutableElement)
                } else null
            }.toMutableSet()
        }

        methods += getSuperProperties {
            it.getMethods()
        }

        return methods
    }

    protected fun ImmutableKmClass.superInterface() = ClassName.bestGuess(name.formatType())

    protected inline fun <T> TypeElement.getSuperProperties(mapper: (TypeElement) -> Collection<T>): Collection<T> {
        val properties = mutableSetOf<T>()
        superclass.asTypeElement()?.let {
            properties += mapper(it)
        }
        interfaces
            .mapNotNull(TypeMirror::asTypeElement)
            .flatMapTo(properties) {
                mapper(it)
            }
        return properties
    }

}