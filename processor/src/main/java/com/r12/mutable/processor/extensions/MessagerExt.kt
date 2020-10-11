package com.r12.mutable.processor.extensions

import javax.annotation.processing.Messager
import javax.tools.Diagnostic

internal fun Messager.printError(message: String) {
    this.printMessage(Diagnostic.Kind.ERROR, message)
}