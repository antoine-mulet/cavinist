package com.amulet.cavinist.service

import com.amulet.cavinist.common.WordSpecUnitTests
import com.amulet.cavinist.persistence.data.RegionEntity
import com.amulet.cavinist.persistence.repository.RegionRepository
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

            "be able to fetch an existing region from the repository" {
                val id = UUID.randomUUID()
                val expected = RegionEntity(id, 0, "region", "country")
                every { regionRepository.findById(id) } returns Mono.just(expected)
                service.getRegion(id) shouldBe expected
            }

            "return null when the region cannot be found in the repository" {
                val id = UUID.randomUUID()
                every { regionRepository.findById(id) } returns Mono.empty()
                service.getRegion(id) should beNull()
            }
        }

        "listRegions" should {

            "be able to fetch all the existing regions from the repository" {
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