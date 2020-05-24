package com.amulet.cavinist.service.wine

import com.amulet.cavinist.common.WordSpecUnitTests
import com.amulet.cavinist.persistence.data.wine.RegionEntity
import com.amulet.cavinist.persistence.repository.wine.RegionRepository
import com.amulet.cavinist.service.wine.RegionService
import io.kotest.matchers.*
import io.kotest.matchers.nulls.beNull
import io.mockk.*
import reactor.core.publisher.*
import java.util.UUID

class RegionServiceTests : WordSpecUnitTests() {

    private val regionRepository = mockk<RegionRepository>()
    private val service = RegionService(regionRepository)

    init {

        "getRegion" should {

            val id = UUID.randomUUID()

            "return the correct region from the repository" {
                val expected = RegionEntity(id, 0, "region", "country")
                every { regionRepository.findById(id) } returns Mono.just(expected)
                service.getRegion(id) shouldBe expected
            }

            "return null when the region cannot be found in the repository" {
                every { regionRepository.findById(id) } returns Mono.empty()
                service.getRegion(id) should beNull()
            }
        }

        "listRegions" should {

            "return all the existing regions from the repository" {
                val expected1 = RegionEntity(UUID.randomUUID(), null, "region 2", "country 1")
                val expected2 = RegionEntity(UUID.randomUUID(), null, "region 2", "country 2")
                val expectedList = listOf(expected1, expected2)
                every { regionRepository.findAll() } returns Flux.fromIterable(expectedList)
                service.listRegions() shouldBe expectedList
            }

            "return an empty list when no region can be found in the repository" {
                every { regionRepository.findAll() } returns Flux.empty()
                service.listRegions() shouldBe emptyList()
            }
        }
    }
}