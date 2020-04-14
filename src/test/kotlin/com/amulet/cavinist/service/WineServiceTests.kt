package com.amulet.cavinist.service

import com.amulet.cavinist.common.WordSpecUnitTests
import com.amulet.cavinist.persistence.data.WineData
import com.amulet.cavinist.persistence.repository.WineRepository
import io.kotest.matchers.*
import io.kotest.matchers.nulls.beNull
import io.mockk.*
import reactor.core.publisher.*
import java.util.UUID

class WineServiceTests : WordSpecUnitTests() {

    private val repository = mockk<WineRepository>()
    private val service = WineService(repository)

    init {
        "getWine" should {

            "be able to fetch an existing wine from the repository" {
                val id = UUID.randomUUID()
                val expected = WineData(id, UUID.randomUUID(), "wine", 1993)
                every { repository.findById(id) } returns Mono.just(expected)
                service.getWine(id) shouldBe expected
            }

            "return null when the wine cannot be found in the repository" {
                val id = UUID.randomUUID()
                every { repository.findById(id) } returns Mono.empty()
                service.getWine(id) should beNull()
            }
        }

        "listWines" should {

            "be able to fetch all the existing wines from the repository" {
                val expected1 = WineData(UUID.randomUUID(), UUID.randomUUID(), "wine 1", 1993)
                val expected2 = WineData(UUID.randomUUID(), UUID.randomUUID(), "wine 2", 2010)
                val expectedList = listOf(expected1, expected2)
                every { repository.findAll() } returns Flux.fromIterable(expectedList)
                service.listWines() shouldBe expectedList
            }

            "return an empty list when no wine can be found in the repository" {
                every { repository.findAll() } returns Flux.empty()
                service.listWines() shouldBe emptyList()
            }
        }

        "createWine" should {

            "save the new wine into the DB" {
                val chateauId = UUID.randomUUID()
                val wineId = UUID.randomUUID()
                val expected = WineData(wineId, chateauId, "New Wine", 2018)
                every {
                    repository.save(
                        WineData(
                            null,
                            chateauId,
                            "New Wine",
                            2018))
                } returns Mono.just(expected)
                service.createWine("New Wine", 2018, chateauId) shouldBe expected
            }
        }
    }
}