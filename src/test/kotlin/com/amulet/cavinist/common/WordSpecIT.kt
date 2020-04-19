package com.amulet.cavinist.common

import io.kotest.core.spec.*
import io.kotest.core.test.TestCaseOrder
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("it")
abstract class WordSpecIT : SpringWordSpec() {

    @Autowired private lateinit var flyway: Flyway

    @Autowired protected lateinit var dataSet: InitialTestDataSet

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        flyway.clean()
        flyway.migrate()
    }

    override fun testCaseOrder(): TestCaseOrder? = TestCaseOrder.Sequential

    override fun isolationMode(): IsolationMode = IsolationMode.SingleInstance
}