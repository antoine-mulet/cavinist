package com.amulet.cavinist.service

import com.amulet.cavinist.common.WordSpecUnitTests
import com.amulet.cavinist.persistence.data.*
import com.amulet.cavinist.persistence.repository.WineryRepository
import io.kotest.matchers.*
import io.kotest.matchers.nulls.beNull
import io.mockk.*
import reactor.core.publisher.*
import java.util.UUID

class WineryServiceTests : WordSpecUnitTests() {

    private val wineryRepository = mockk<WineryRepository>()
    private val service = WineryService(wineryRepository)

    init {

        "getWinery" should {

            "be able to fetch an existing winery from the repository" {
                val id = UUID.randomUUID()
                val expected = WineryEntity(id, 0, "winery", UUID.randomUUID())
                every { wineryRepository.findById(id) } returns Mono.just(expected)
                service.getWinery(id) shouldBe expected
            }

            "return null when the winery cannot be found in the repository" {
                val id = UUID.randomUUID()
                every { wineryRepository.findById(id) } returns Mono.empty()
                service.getWinery(id) should beNull()
            }
        }

        "listWineries" should {

            "be able to fetch all the existing wineries from the repository" {
                val expected1 = WineryWithDependencies(
                    UUID.randomUUID(),
                    0,
                    "winery 1",
                    RegionEntity(UUID.randomUUID(), null, "region 1", "country 1"))
                val expected2 = WineryWithDependencies(
                    UUID.randomUUID(),
                    0,
                    "winery 2",
                    RegionEntity(UUID.randomUUID(), null, "region 2", "country 2"))
                val expectedList = listOf(expected1, expected2)
                every { wineryRepository.findAllWithDependencies() } returns Flux.fromIterable(
                    expectedList)
                service.listWineries() shouldBe expectedList
            }

            "return an empty list when no winery can be found in the repository" {
                every { wineryRepository.findAllWithDependencies() } returns Flux.empty()
                service.listWineries() shouldBe emptyList()
            }
        }
    }
}