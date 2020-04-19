package com.amulet.cavinist.common

import io.kotest.core.spec.style.WordSpec
import io.kotest.spring.SpringListener
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
abstract class SpringWordSpec : WordSpec() {

    override fun listeners() = listOf(SpringListener)
}