package com.amulet.cavinist.common

import io.kotest.core.config.AbstractProjectConfig

class KotestConfig : AbstractProjectConfig() {

    override val parallelism: Int = System.getProperty("parallelism")?.toInt() ?: 1

}