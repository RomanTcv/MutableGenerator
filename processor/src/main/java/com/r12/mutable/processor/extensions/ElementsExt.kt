package com.r12.mutable.processor.extensions

import com.r12.mutable.annotations.Mutable
import javax.lang.model.element.Element
import javax.lang.model.util.Elements

internal const val MUTABLE = "Mutable"

internal fun Element.fileName(): String {
    val annotation = getAnnotation(Mutable::class.java)
    if (annotation.className.isNotBlank()) return annotation.className

    val name = this.simpleName.toString()
    if (annotation.mutableSuffix) return name.removeSuffix(MUTABLE) + MUTABLE

    return MUTABLE + name.removePrefix(MUTABLE)
}

internal fun Element.getPackage(elements: Elements): String = elements.packageOf(this)

internal fun Elements.packageOf(element: Element) = getPackageOf(element).toString()