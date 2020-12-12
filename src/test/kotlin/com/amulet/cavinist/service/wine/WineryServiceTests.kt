package com.amulet.cavinist.service.wine

import com.amulet.cavinist.common.WordSpecUnitTests
import com.amulet.cavinist.persistence.data.wine.*
import com.amulet.cavinist.persistence.repository.wine.WineryRepository
import com.amulet.cavinist.service.*
import com.amulet.cavinist.utils.suspending
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.*
import reactor.core.publisher.*
import java.util.UUID

class WineryServiceTests : WordSpecUnitTests() {

    private val wineryRepository = mockk<WineryRepository>()
    private val regionService = mockk<RegionService>()
    private val entityFactory = mockk<WineEntityFactory>()
    private val transactionManager = mockk<TxManager>()
    private val service = WineryService(wineryRepository, regionService, entityFactory, transactionManager)

    init {

        every { transactionManager.newTx(any<Mono<WineryEntity>>()) } answers { firstArg() }

        "getWinery" should {

            "return the correct winery coming from the repository" {
                val id = UUID.randomUUID()
                val userId = UUID.randomUUID()
                val expected = WineryEntity(id, 0, "winery", UUID.randomUUID(), userId)
                every { wineryRepository.findForUser(id, userId) } returns Mono.just(expected)
                service.getWinery(id, userId).suspending() shouldBe expected
            }
        }

        "listWineries" should {

            "return all the existing wineries coming from the repository" {
                val userId = UUID.randomUUID()
                val expected1 = WineryWithDependencies(
                    UUID.randomUUID(), 0, "winery 1",
                    RegionEntity(UUID.randomUUID(), null, "region 1", "country 1", userId)
                                                      )
                val expected2 = WineryWithDependencies(
                    UUID.randomUUID(), 0, "winery 2",
                    RegionEntity(UUID.randomUUID(), null, "region 2", "country 2", userId)
                                                      )
                val expectedList = listOf(expected1, expected2)
                every { wineryRepository.findAllForUser(userId) } returns Flux.fromIterable(expectedList)
                service.listWineries(userId).suspending() shouldBe expectedList
            }
        }

        "createWinery" should {
            val userId = UUID.randomUUID()

            "save a new winery referencing an existing region" {
                val regionId = UUID.randomUUID()
                val regionEntity = RegionEntity(regionId, 0, "region", "country", userId)
                every { regionService.getRegion(regionId, userId) } returns Mono.just(regionEntity)
                val wineryId = UUID.randomUUID()
                val winery = WineryEntity(wineryId, null, "New Winery", regionId, userId)
                every { entityFactory.newWinery("New Winery", regionId, userId) } returns winery
                every { wineryRepository.save(winery) } returns Mono.just(winery.copy(version = 0))
                val res = service.createWinery(
                    NewWinery("New Winery", ExistingRegion(regionId)), userId)
                    .suspending()
                res shouldBe winery.copy(version = 0)

                verify(exactly = 1) { transactionManager.newTx(any<Mono<WineEntity>>()) }
            }

            "save a new winery along with its new region" {
                val wineryRegionId = UUID.randomUUID()
                val wineryRegionEntity = RegionEntity(wineryRegionId, 0, "winery_region", "country", userId)
                val newWineryRegion = NewRegion("winery_region", "country")
                every { regionService.createRegion(newWineryRegion, userId) } returns Mono.just(wineryRegionEntity)
                val wineryId = UUID.randomUUID()
                val winery = WineryEntity(wineryId, null, "New Winery", wineryRegionId, userId)
                every { entityFactory.newWinery("New Winery", wineryRegionId, userId) } returns winery
                every { wineryRepository.save(winery) } returns Mono.just(winery.copy(version = 0))
                val res = service.createWinery(
                    NewWinery("New Winery", NewRegion("winery_region", "country")), userId)
                    .suspending()
                res shouldBe winery.copy(version = 0)

                verify(exactly = 1) { transactionManager.newTx(any<Mono<WineEntity>>()) }
            }

            "fail if a reference cannot be found" {
                val wineryRegionId = UUID.randomUUID()
                every { regionService.getRegion(wineryRegionId, userId) } returns Mono.empty()
                val exception = shouldThrow<ObjectNotFoundException> {
                    service.createWinery(
                        NewWinery("New Winery", ExistingRegion(wineryRegionId)), userId)
                        .suspending()
                }
                exception shouldHaveMessage "Winery region with id '$wineryRegionId' not found for user $userId."

                verify(exactly = 1) { transactionManager.newTx(any<Mono<WineryEntity>>()) }
            }
        }
    }
}