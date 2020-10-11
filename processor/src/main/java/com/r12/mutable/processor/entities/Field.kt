package com.r12.mutable.processor.entities

import com.r12.mutable.processor.extensions.formatType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.metadata.*
import kotlinx.metadata.KmClassifier

@KotlinPoetMetadataPreview
internal class Field(private val property: ImmutableKmProperty) {

    val name: String = property.name
    private val typeName: TypeName

    private val modifiers: MutableList<KModifier> = mutableListOf(KModifier.OVERRIDE).apply {
        if (property.isInternal) add(KModifier.INTERNAL)
        if (property.isProtected) add(KModifier.PROTECTED)
    }

    init {
        val classifier = property.returnType.classifier as KmClassifier.Class
        val className = ClassName.bestGuess(classifier.name.formatType())
        val typeParameters = property.typeParameters.map {
            ClassName.bestGuess(it.name.formatType())
        }
        typeName =
            (if (typeParameters.isEmpty()) className else className.parameterizedBy(typeParameters))
                .copy(property.returnType.isNullable)
    }

    fun toParameterSpec(): ParameterSpec = ParameterSpec.builder(name, typeName).build()

    fun toPropertySpec(): PropertySpec {
        return PropertySpec.builder(name, typeName)
            .mutable(true)
            .initializer(name)
            .addModifiers(modifiers)
            .build()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Field

        if (property.name != other.property.name) return false

        return true
    }

    override fun hashCode(): Int {
        return property.name.hashCode()
    }
}