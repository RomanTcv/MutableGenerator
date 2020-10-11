package com.r12.mutable.annotations

/**
 * @author R12rus
 */
@Retention(AnnotationRetention.SOURCE)
@Target(allowedTargets = [AnnotationTarget.CLASS])
annotation class Mutable(val withPrefixName: Boolean = false)