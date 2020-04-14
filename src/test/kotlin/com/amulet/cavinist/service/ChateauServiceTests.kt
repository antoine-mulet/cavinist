package com.amulet.cavinist.service

import com.amulet.cavinist.common.WordSpecUnitTests
import com.amulet.cavinist.persistence.data.ChateauData
import com.amulet.cavinist.persistence.repository.ChateauRepository
import io.kotest.matchers.*
import io.kotest.matchers.nulls.beNull
import io.mockk.*
import reactor.core.publisher.*
import java.util.UUID

class ChateauServiceTests : WordSpecUnitTests() {

    private val repository = mockk<ChateauRepository>()
    private val service = ChateauService(repository)

    init {

        "getChateau" should {

            "be able to fetch an existing chateau from the repository" {
                val id = UUID.randomUUID()
                val expected = ChateauData(id, "chateau", "region")
                every { repository.findById(id) } returns Mono.just(expected)
                service.getChateau(id) shouldBe expected
            }

            "return null when the chateau cannot be found in the repository" {
                val id = UUID.randomUUID()
                every { repository.findById(id) } returns Mono.empty()
                service.getChateau(id) should beNull()
            }
        }

        "listChateaux" should {

            "be able to fetch all the existing chateaux from the repository" {
                val expected1 = ChateauData(UUID.randomUUID(), "chateau 1", "region")
                val expected2 = ChateauData(UUID.randomUUID(), "chateau 2", "region")
                val expectedList = listOf(expected1, expected2)
                every { repository.findAll() } returns Flux.fromIterable(expectedList)
                service.listChateaux() shouldBe expectedList
            }

            "return an empty list when no chateau can be found in the repository" {
                every { repository.findAll() } returns Flux.empty()
                service.listChateaux() shouldBe emptyList()
            }
        }

        "createChateau" should {

            "save the new chateau into the DB" {
                val chateauId = UUID.randomUUID()
                val expected = ChateauData(chateauId, "New Chateau", "region")
                every { repository.save(ChateauData(null, "New Chateau", "region")) } returns Mono.just(expected)
                service.createChateau("New Chateau", "region") shouldBe expected
            }
        }
    }
}