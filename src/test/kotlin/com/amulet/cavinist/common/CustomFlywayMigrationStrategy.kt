package com.amulet.cavinist.common

import org.flywaydb.core.Flyway
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.springframework.context.annotation.*

@Configuration
@Profile("it")
class CustomFlywayMigrationStrategy : FlywayMigrationStrategy {

    /**
     * Does nothing since clean/migrate is performed before each spec - see [WordSpecWebIT]
     */
    override fun migrate(flyway: Flyway?) = Unit
}