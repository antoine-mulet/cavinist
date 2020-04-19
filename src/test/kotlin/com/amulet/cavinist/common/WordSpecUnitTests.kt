package com.amulet.cavinist.common

import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.TestCaseOrder
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("unit")
abstract class WordSpecUnitTests : SpringWordSpec() {

    override fun testCaseOrder(): TestCaseOrder? = TestCaseOrder.Random

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf
}