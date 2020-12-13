package com.r12.mutable.processor.factories

import com.r12.mutable.processor.entities.Field
import com.r12.mutable.processor.extensions.fileName
import com.r12.mutable.processor.extensions.formatType
import com.r12.mutable.processor.extensions.getPackage
import com.r12.mutable.processor.extensions.isDataClass
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.metadata.ImmutableKmClass
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isInternal
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

@KotlinPoetMetadataPreview
internal class InterfaceGeneratorFactory(
    elements: Elements
) : BaseFactory(elements) {

    fun generate(element: TypeElement, kmClass: ImmutableKmClass): FileSpec {
        val fileName = element.fileName()
        val packageName = element.getPackage(elements)
        val fullPath = "$packageName.$fileName"

        val fields = element.getFields()
        val isAbstract = element.isAbstract()
        val isDataClass = !isAbstract && element.isDataClass()

        val typeSpec = TypeSpec
            .classBuilder(fileName)
            .addSuperinterface(kmClass.superInterface())
            .addProperties(fields.map(Field::toPropertySpec))
            .primaryConstructor(fields.generateConstructor())
            .addModifiers(kmClass.getModifiers(isAbstract, isDataClass))
            .addExtension(element, fullPath, isAbstract)
            .build()

        return FileSpec.builder(packageName, fileName)
            .addType(typeSpec)
            .addExtension(element, fields, fullPath, isAbstract)
            .build()
    }

    private fun TypeSpec.Builder.addExtension(element: TypeElement, fullPath: String, isAbstract: Boolean): TypeSpec.Builder {
        if (isAbstract) element.createAbstractExtension(fullPath)
        return this
    }

    private fun FileSpec.Builder.addExtension(
        element: TypeElement,
        fields: Collection<Field>,
        fullPath: String,
        isAbstract: Boolean
    ): FileSpec.Builder {
        if (!isAbstract) addFunction(element.createExtension(fields, fullPath))
        return this
    }

    private fun TypeElement.createAbstractExtension(fullPath: String): FunSpec {
        return createDefaultExtensionBuilder(fullPath)
            .addModifiers(KModifier.ABSTRACT)
            .build()
    }

    private fun TypeElement.createExtension(fields: Collection<Field>, fullPath: String): FunSpec {
        return createDefaultExtensionBuilder(fullPath)
            .addFields(fields, fullPath)
            .build()
    }

    private fun TypeElement.createDefaultExtensionBuilder(fullPath: String): FunSpec.Builder {
        val className = ClassName.bestGuess(simpleName.toString().formatType())

        return FunSpec.builder("toMutable")
            .receiver(className)
            .returns(ClassName.bestGuess(fullPath))
    }

    private fun FunSpec.Builder.addFields(fields: Collection<Field>, fullPath: String): FunSpec.Builder {
        this.addStatement("if (this is $fullPath) return this")
            .addStatement("return $fullPath(")
            .addStatement(
                fields.joinToString {
                    "this.${it.name}"
                }
            )
            .addStatement(")")

        return this
    }

    private fun ImmutableKmClass.getModifiers(isAbstract: Boolean, isDataClass: Boolean): List<KModifier> {
        val list = mutableListOf<KModifier>()

        val kModifier = if (isAbstract) {
            KModifier.ABSTRACT
        } else {
            if (isDataClass) KModifier.DATA else KModifier.OPEN
        }
        list.add(kModifier)
        if (isInternal) list.add(KModifier.INTERNAL)

        return list
    }

    private fun TypeElement.isAbstract() = getMethods().firstOrNull { it.isAbstract } != null

    private fun Collection<Field>.generateConstructor(): FunSpec {
        return FunSpec.constructorBuilder()
            .addParameters(map(Field::toParameterSpec))
            .build()
    }
}