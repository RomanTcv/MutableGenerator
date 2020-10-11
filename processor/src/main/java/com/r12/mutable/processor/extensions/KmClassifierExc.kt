package com.r12.mutable.processor.extensions

import com.squareup.kotlinpoet.ClassName
import kotlinx.metadata.KmClassifier

internal val KmClassifier.className: ClassName?
    get() = (this as? KmClassifier.Class)?.let {
        ClassName.bestGuess(it.name.formatType())
    }