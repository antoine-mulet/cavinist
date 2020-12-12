package com.amulet.cavinist.service.wine

import com.amulet.cavinist.common.WordSpecUnitTests
import com.amulet.cavinist.persistence.data.wine.*
import com.amulet.cavinist.persistence.repository.wine.RegionRepository
import com.amulet.cavinist.utils.suspending
import io.kotest.matchers.*
import io.mockk.*
import reactor.core.publisher.*
import java.util.UUID

class RegionServiceTests : WordSpecUnitTests() {

    private val regionRepository = mockk<RegionRepository>()
    private val entityFactory = mockk<WineEntityFactory>()
    private val service = RegionService(regionRepository, entityFactory)

    init {

        "getRegion" should {

            "return the correct region coming from the repository" {
                val id = UUID.randomUUID()
                val userId = UUID.randomUUID()
                val expected = RegionEntity(id, 0, "region", "country", userId)
                every { regionRepository.findForUser(id, userId) } returns Mono.just(expected)
                service.getRegion(id, userId).suspending() shouldBe expected
            }
        }

        "listRegions" should {

            "return all the existing regions coming from the repository for a given user" {
                val userId = UUID.randomUUID()
                val expected1 = RegionEntity(UUID.randomUUID(), null, "region 2", "country 1", userId)
                val expected2 = RegionEntity(UUID.randomUUID(), null, "region 2", "country 2", userId)
                val expectedList = listOf(expected1, expected2)
                every { regionRepository.findAllForUser(userId) } returns Flux.fromIterable(expectedList)
                service.listRegions(userId).suspending() shouldBe expectedList
            }
        }
    }
}