package com.amulet.cavinist.common

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.spring.SpringListener

abstract class WordSpecUnitTests : WordSpec() {

    override fun listeners() = listOf(SpringListener)

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf
}