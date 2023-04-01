package com.amulet.cavinist.service.wine

import com.amulet.cavinist.common.WordSpecUnitTests
import com.amulet.cavinist.persistence.data.wine.RegionEntity
import com.amulet.cavinist.persistence.data.wine.WineEntityFactory
import com.amulet.cavinist.persistence.data.wine.WineryEntity
import com.amulet.cavinist.persistence.data.wine.WineryWithDependencies
import com.amulet.cavinist.persistence.repository.wine.WineryRepository
import com.amulet.cavinist.service.ObjectNotFoundException
import com.amulet.cavinist.service.TxManager
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.coEvery
import io.mockk.coInvoke
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.util.UUID

class WineryServiceTests : WordSpecUnitTests() {

    private val wineryRepository = mockk<WineryRepository>()
    private val regionService = mockk<RegionService>()
    private val entityFactory = mockk<WineEntityFactory>()
    private val transactionManager = mockk<TxManager>()
    private val service = WineryService(wineryRepository, regionService, entityFactory, transactionManager)

    init {

        coEvery {
            transactionManager.newTx(captureCoroutine<suspend () -> WineryEntity>())
        } answers {
            coroutine<suspend () -> WineryEntity>().coInvoke()
        }

        "getWinery" should {

            "return the correct winery coming from the repository" {
                val id = UUID.randomUUID()
                val userId = UUID.randomUUID()
                val expected = WineryEntity(id, 0, "winery", UUID.randomUUID(), userId)
                coEvery { wineryRepository.findForUser(id, userId) } returns expected
                service.getWinery(id, userId) shouldBe expected
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
                coEvery { wineryRepository.findAllForUser(userId) } returns expectedList
                service.listWineries(userId) shouldBe expectedList
            }
        }

        "createWinery" should {
            val userId = UUID.randomUUID()

            "save a new winery referencing an existing region" {
                val regionId = UUID.randomUUID()
                val regionEntity = RegionEntity(regionId, 0, "region", "country", userId)
                coEvery { regionService.getRegion(regionId, userId) } returns regionEntity
                val wineryId = UUID.randomUUID()
                val winery = WineryEntity(wineryId, null, "New Winery", regionId, userId)
                every { entityFactory.newWinery("New Winery", regionId, userId) } returns winery
                coEvery { wineryRepository.save(winery) } returns winery.copy(version = 0)
                val res = service.createWinery(
                    NewWinery("New Winery", ExistingRegion(regionId)), userId
                )

                res shouldBe winery.copy(version = 0)

                coVerify(exactly = 1) { transactionManager.newTx(any<suspend () -> WineryEntity>()) }
            }

            "save a new winery along with its new region" {
                val wineryRegionId = UUID.randomUUID()
                val wineryRegionEntity = RegionEntity(wineryRegionId, 0, "winery_region", "country", userId)
                val newWineryRegion = NewRegion("winery_region", "country")
                coEvery { regionService.createRegion(newWineryRegion, userId) } returns wineryRegionEntity
                val wineryId = UUID.randomUUID()
                val winery = WineryEntity(wineryId, null, "New Winery", wineryRegionId, userId)
                every { entityFactory.newWinery("New Winery", wineryRegionId, userId) } returns winery
                coEvery { wineryRepository.save(winery) } returns winery.copy(version = 0)
                val res = service.createWinery(
                    NewWinery("New Winery", NewRegion("winery_region", "country")), userId
                )

                res shouldBe winery.copy(version = 0)

                coVerify(exactly = 1) { transactionManager.newTx(any<suspend () -> WineryEntity>()) }
            }

            "fail if a reference cannot be found" {
                val wineryRegionId = UUID.randomUUID()
                coEvery { regionService.getRegion(wineryRegionId, userId) } throws ObjectNotFoundException(
                    "Winery region with id '$wineryRegionId' not found for user $userId."
                )
                val exception = shouldThrow<ObjectNotFoundException> {
                    service.createWinery(
                        NewWinery("New Winery", ExistingRegion(wineryRegionId)), userId
                    )

                }
                exception shouldHaveMessage "Winery region with id '$wineryRegionId' not found for user $userId."

                coVerify(exactly = 1) { transactionManager.newTx(any<suspend () -> WineryEntity>()) }
            }
        }
    }
}