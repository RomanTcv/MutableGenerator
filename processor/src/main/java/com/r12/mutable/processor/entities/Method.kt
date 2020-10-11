package com.r12.mutable.processor.entities

import com.r12.mutable.processor.extensions.asTypeElement
import com.r12.mutable.processor.extensions.className
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.metadata.ImmutableKmFunction
import com.squareup.kotlinpoet.metadata.ImmutableKmType
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isAbstract
import kotlinx.metadata.KmClassifier
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier

@KotlinPoetMetadataPreview
internal data class Method(
    val name: String,
    val parameters: List<ClassName>,
    val returnType: ClassName?,
    val isAbstract: Boolean
) {

    internal constructor(
        func: ImmutableKmFunction
    ) : this(
        func.name,
        func.getParametersClassNames(),
        func.returnType.toClassName(),
        func.isAbstract
    )

    internal constructor(
        element: ExecutableElement
    ) : this(
        element.simpleName.toString(),
        element.getParametersClassNames(),
        element.getReturnClassName(),
        element.modifiers.contains(Modifier.ABSTRACT)
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Method

        if (name != other.name) return false
        if (parameters != other.parameters) return false
        if (returnType != other.returnType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (parameters.hashCode())
        result = 31 * result + (returnType?.hashCode() ?: 0)
        return result
    }
}

private fun ExecutableElement.getReturnClassName(): ClassName? {
    return returnType?.asTypeElement()?.let {
        ClassName.bestGuess(it.toString())
    }
}

private fun ExecutableElement.getParametersClassNames(): List<ClassName> {
    return parameters.mapNotNull {
        it.asType().asTypeElement()?.toString()
    }.map(ClassName::bestGuess)
}

@KotlinPoetMetadataPreview
private fun ImmutableKmType.toClassName(): ClassName? = classifier.className

@KotlinPoetMetadataPreview
private fun ImmutableKmFunction.getParametersClassNames(): List<ClassName> {
    return valueParameters.mapNotNull {
        it.type?.classifier as? KmClassifier.Class
    }.mapNotNull(KmClassifier.Class::className)
}