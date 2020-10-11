package com.r12.mutable.processor.extensions

import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror

internal fun TypeMirror.asTypeElement(): TypeElement? =
    (this as? DeclaredType)?.asElement() as? TypeElement