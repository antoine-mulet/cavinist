package com.amulet.cavinist.persistence.data

import org.springframework.data.domain.Persistable
import java.util.UUID

abstract class Entity : Persistable<UUID> {

    abstract val ID: UUID

    fun name(): String = this.javaClass.simpleName.dropLast(6) // dropping 'Entity' at the end

    abstract fun description(): String

    override fun getId(): UUID? = ID
}