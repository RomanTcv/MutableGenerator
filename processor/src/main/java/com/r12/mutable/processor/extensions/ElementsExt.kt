package com.r12.mutable.processor.extensions

import com.r12.mutable.annotations.Mutable
import javax.lang.model.element.Element
import javax.lang.model.util.Elements

internal const val MUTABLE = "Mutable"

internal fun Element.fileName(): String {
    val name = this.simpleName.toString()
    val withPrefixName = getAnnotation(Mutable::class.java).withPrefixName
    if (withPrefixName) return "Mutable" + name.removePrefix(MUTABLE)
    return name.removeSuffix(MUTABLE) + MUTABLE
}

internal fun Element.getPackage(elements: Elements): String {
    return elements.packageOf(this)
}

internal fun Elements.packageOf(element: Element) = getPackageOf(element).toString()