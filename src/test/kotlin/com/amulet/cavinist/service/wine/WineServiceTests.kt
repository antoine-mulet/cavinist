package com.amulet.cavinist.service.wine

import com.amulet.cavinist.common.WordSpecUnitTests
import com.amulet.cavinist.persistence.data.wine.RegionEntity
import com.amulet.cavinist.persistence.data.wine.WineEntity
import com.amulet.cavinist.persistence.data.wine.WineEntityFactory
import com.amulet.cavinist.persistence.data.wine.WineType
import com.amulet.cavinist.persistence.data.wine.WineWithDependencies
import com.amulet.cavinist.persistence.data.wine.WineryEntity
import com.amulet.cavinist.persistence.data.wine.WineryWithDependencies
import com.amulet.cavinist.persistence.repository.wine.WineRepository
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

class WineServiceTests : WordSpecUnitTests() {

    private val wineRepository = mockk<WineRepository>()
    private val wineryService = mockk<WineryService>()
    private val regionService = mockk<RegionService>()
    private val entityFactory = mockk<WineEntityFactory>()
    private val transactionManager = mockk<TxManager>()
    private val service =
        WineService(wineRepository, wineryService, regionService, entityFactory, transactionManager)

    init {

        coEvery {
            transactionManager.newTx(captureCoroutine<suspend () -> WineEntity>())
        } answers {
            coroutine<suspend () -> WineEntity>().coInvoke()
        }

        "getWine" should {

            "return the correct wine coming from the repository" {
                val id = UUID.randomUUID()
                val userId = UUID.randomUUID()
                val expected = WineEntity(id, 0, "wine", WineType.WHITE, UUID.randomUUID(), UUID.randomUUID(), userId)
                coEvery { wineRepository.findForUser(id, userId) } returns expected
                service.findWine(id, userId) shouldBe expected
            }
        }

        "listWines" should {

            "return all the existing wines coming from the repository" {
                val userId = UUID.randomUUID()
                val regionId = UUID.randomUUID()
                val regionEntity = RegionEntity(regionId, 0, "region", "country", userId)
                val wineryId = UUID.randomUUID()
                val wineryEntity = WineryEntity(wineryId, 0, "winery", regionId, userId)
                val wineryWithDependencies =
                    WineryWithDependencies(wineryEntity.ID, 0, wineryEntity.name, regionEntity)
                val wineEntity1 = WineEntity(UUID.randomUUID(), 1, "wine", WineType.WHITE, wineryId, regionId, userId)
                val wineEntity2 = WineEntity(UUID.randomUUID(), 2, "wine", WineType.WHITE, wineryId, regionId, userId)
                val expectedList = listOf(
                    WineWithDependencies(
                        wineEntity1.ID,
                        0,
                        wineEntity1.name,
                        wineEntity1.type,
                        wineryWithDependencies,
                        regionEntity
                    ),
                    WineWithDependencies(
                        wineEntity2.ID,
                        0,
                        wineEntity2.name,
                        wineEntity2.type,
                        wineryWithDependencies,
                        regionEntity
                    )
                )
                coEvery { wineRepository.findAllForUser(userId) } returns expectedList
                service.listWines(userId) shouldBe expectedList
            }
        }

        "createWine" should {

            val userId = UUID.randomUUID()

            "save a new wine referencing existing winery and region" {
                val regionId = UUID.randomUUID()
                val regionEntity = RegionEntity(regionId, 0, "region", "country", userId)
                coEvery { regionService.getRegion(regionId, userId) } returns regionEntity
                val wineryId = UUID.randomUUID()
                val wineryEntity = WineryEntity(wineryId, 0, "winery", regionId, userId)
                coEvery { wineryService.getWinery(wineryId, userId) } returns wineryEntity
                val wineId = UUID.randomUUID()
                val wine = WineEntity(wineId, null, "New Wine", WineType.ROSE, wineryId, regionId, userId)
                every { entityFactory.newWine("New Wine", WineType.ROSE, wineryId, regionId, userId) } returns wine
                coEvery { wineRepository.save(wine) } returns wine.copy(version = 0)
                val res = service.createWine(
                    NewWine("New Wine", WineType.ROSE, ExistingWinery(wineryId), ExistingRegion(regionId)), userId
                )

                res shouldBe wine.copy(version = 0)

                coVerify(exactly = 1) { transactionManager.newTx(any<suspend () -> WineEntity>()) }
            }

            "save a new wine along with its winery and region" {
                val wineRegionId = UUID.randomUUID()
                val wineRegionEntity = RegionEntity(wineRegionId, 0, "wine_region", "country", userId)
                val newWineRegion = NewRegion("wine_region", "country")
                val wineryRegionId = UUID.randomUUID()
                val wineryRegionEntity = RegionEntity(wineryRegionId, 0, "winery_region", "country", userId)
                val newWineryRegion = NewRegion("winery_region", "country")
                val wineryId = UUID.randomUUID()
                val wineryEntity = WineryEntity(wineryId, 0, "winery", wineryRegionId, userId)
                val newWinery = NewWinery("winery", newWineryRegion)
                val wineId = UUID.randomUUID()
                val wine = WineEntity(wineId, null, "New Wine", WineType.ROSE, wineryId, wineRegionId, userId)
                every { entityFactory.newWine("New Wine", WineType.ROSE, wineryId, wineRegionId, userId) } returns wine
                coEvery { wineRepository.save(wine) } returns wine.copy(version = 0)
                coEvery { regionService.createRegion(newWineRegion, userId) } returns wineRegionEntity
                coEvery { regionService.createRegion(newWineryRegion, userId) } returns wineryRegionEntity
                coEvery { wineryService.createWinery(newWinery, userId) } returns wineryEntity
                val res = service.createWine(
                    NewWine(
                        "New Wine",
                        WineType.ROSE,
                        NewWinery("winery", NewRegion("winery_region", "country")),
                        NewRegion("wine_region", "country")
                    ), userId
                )
                res shouldBe wine.copy(version = 0)

                coVerify(exactly = 1) { transactionManager.newTx(any<suspend () -> WineEntity>()) }
            }

            "save a new wine along with its winery and region when the wine and winery regions are the same" {
                val regionId = UUID.randomUUID()
                val regionEntity = RegionEntity(regionId, 0, "region", "country", userId)
                val newRegion = NewRegion("region", "country")
                val wineryId = UUID.randomUUID()
                val wineryEntity = WineryEntity(wineryId, 0, "winery", regionId, userId)
                val newWinery = NewWinery("winery", newRegion)
                val wineId = UUID.randomUUID()
                val wine = WineEntity(wineId, null, "New Wine", WineType.ROSE, wineryId, regionId, userId)
                every { entityFactory.newWine("New Wine", WineType.ROSE, wineryId, regionId, userId) } returns wine
                coEvery { wineRepository.save(wine) } returns wine.copy(version = 0)
                coEvery { regionService.createRegion(newRegion, userId) } returns regionEntity
                coEvery { wineryService.createWinery(newWinery, userId) } returns wineryEntity
                val res = service.createWine(
                    NewWine(
                        "New Wine",
                        WineType.ROSE,
                        NewWinery("winery", NewRegion("region", "country")),
                        NewRegion("region", "country")
                    ), userId
                )
                res shouldBe wine.copy(version = 0)

                coVerify(exactly = 1) { transactionManager.newTx(any<suspend () -> WineEntity>()) }
            }

            "fail if a reference cannot be found" {
                val wineRegionId = UUID.randomUUID()
                val wineRegion = RegionEntity(wineRegionId, 0, "wine_region", "country", userId)
                val wineryRegionId = UUID.randomUUID()
                val wineryId = UUID.randomUUID()
                val winery = WineryEntity(wineryId, 0, "winery", wineryRegionId, userId)
                coEvery { regionService.getRegion(wineRegionId, userId) } throws ObjectNotFoundException(
                    "Wine region with id '$wineRegionId' not found for user $userId."
                )
                coEvery { wineryService.getWinery(wineryId, userId) } returns winery
                val exception1 = shouldThrow<ObjectNotFoundException> {
                    service.createWine(
                        NewWine(
                            "New Wine",
                            WineType.ROSE,
                            ExistingWinery(wineryId),
                            ExistingRegion(wineRegionId)
                        ), userId
                    )
                }
                exception1 shouldHaveMessage "Wine region with id '$wineRegionId' not found for user $userId."

                coEvery { regionService.getRegion(wineRegionId, userId) } returns wineRegion
                coEvery { wineryService.getWinery(wineryId, userId) } throws ObjectNotFoundException(
                    "Winery with id '$wineryId' not found."
                )
                val exception2 = shouldThrow<ObjectNotFoundException> {
                    service.createWine(
                        NewWine(
                            "New Wine",
                            WineType.ROSE,
                            ExistingWinery(wineryId),
                            ExistingRegion(wineRegionId)
                        ), userId
                    )
                }
                exception2 shouldHaveMessage "Winery with id '$wineryId' not found."
            }
        }
    }
}