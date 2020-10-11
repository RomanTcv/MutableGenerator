package com.r12.mutable_generator

import com.r12.mutable.annotations.Mutable
import java.io.Serializable

@Mutable
interface Inspection : Identifiable<String>, Serializable {

    override val id: String
    val kek: String?

}

interface Identifiable<T> {

    val id: T

    val item: Long

}