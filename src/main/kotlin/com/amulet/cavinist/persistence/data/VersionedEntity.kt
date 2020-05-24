package com.amulet.cavinist.persistence.data

abstract class VersionedEntity : Entity() {

    abstract val version: Int?

    fun version(): Int = version ?: 0

    override fun isNew(): Boolean = version == null

}