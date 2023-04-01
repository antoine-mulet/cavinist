package com.amulet.cavinist.service.wine

import com.amulet.cavinist.common.WordSpecUnitTests
import com.amulet.cavinist.persistence.data.wine.RegionEntity
import com.amulet.cavinist.persistence.data.wine.WineEntityFactory
import com.amulet.cavinist.persistence.repository.wine.RegionRepository
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
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
                coEvery { regionRepository.findForUser(id, userId) } returns expected
                service.getRegion(id, userId) shouldBe expected
            }
        }

        "listRegions" should {

            "return all the existing regions coming from the repository for a given user" {
                val userId = UUID.randomUUID()
                val expected1 = RegionEntity(UUID.randomUUID(), null, "region 2", "country 1", userId)
                val expected2 = RegionEntity(UUID.randomUUID(), null, "region 2", "country 2", userId)
                val expectedList = listOf(expected1, expected2)
                coEvery { regionRepository.findAllForUser(userId) } returns expectedList
                service.listRegions(userId) shouldBe expectedList
            }
        }
    }
}