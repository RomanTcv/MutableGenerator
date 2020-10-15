package com.r12.mutable.annotations

/**
 * @author R12rus
 *
 *  className - Your own custom class name
 *  mutableSuffix - If className is blank will generate default name, for instance - MutableInspection or InspectionMutable.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(allowedTargets = [AnnotationTarget.CLASS])
annotation class Mutable(val className: String = "", val mutableSuffix: Boolean = true)