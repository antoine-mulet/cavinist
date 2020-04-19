package com.amulet.cavinist.persistence.data

import java.util.UUID

abstract class Entity {

    abstract val id: UUID

    protected abstract var version: Int? // Must be a var - see https://github.com/spring-projects/spring-data-r2dbc/issues/365

    abstract fun description(): String

    fun version(): Int = version ?: 0

}