package com.amulet.cavinist.service.wine

import com.amulet.cavinist.common.WordSpecUnitTests
import com.amulet.cavinist.persistence.data.wine.*
import com.amulet.cavinist.persistence.repository.wine.WineRepository
import com.amulet.cavinist.service.*
import com.amulet.cavinist.utils.suspending
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.*
import reactor.core.publisher.*
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

        every { transactionManager.newTx(any<Mono<WineEntity>>()) } answers { firstArg() }

        "getWine" should {

            "return the correct wine coming from the repository" {
                val id = UUID.randomUUID()
                val userId = UUID.randomUUID()
                val expected = WineEntity(id, 0, "wine", WineType.WHITE, UUID.randomUUID(), UUID.randomUUID(), userId)
                every { wineRepository.findForUser(id, userId) } returns Mono.just(expected)
                service.getWine(id, userId).suspending() shouldBe expected
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
                        regionEntity),
                    WineWithDependencies(
                        wineEntity2.ID,
                        0,
                        wineEntity2.name,
                        wineEntity2.type,
                        wineryWithDependencies,
                        regionEntity))
                every { wineRepository.findAllForUser(userId) } returns Flux.fromIterable(expectedList)
                service.listWines(userId).suspending() shouldBe expectedList
            }
        }

        "createWine" should {

            val userId = UUID.randomUUID()

            "save a new wine referencing existing winery and region" {
                val regionId = UUID.randomUUID()
                val regionEntity = RegionEntity(regionId, 0, "region", "country", userId)
                every { regionService.getRegion(regionId, userId) } returns Mono.just(regionEntity)
                val wineryId = UUID.randomUUID()
                val wineryEntity = WineryEntity(wineryId, 0, "winery", regionId, userId)
                every { wineryService.getWinery(wineryId, userId) } returns Mono.just(wineryEntity)
                val wineId = UUID.randomUUID()
                val wine = WineEntity(wineId, null, "New Wine", WineType.ROSE, wineryId, regionId, userId)
                every { entityFactory.newWine("New Wine", WineType.ROSE, wineryId, regionId, userId) } returns wine
                every { wineRepository.save(wine) } returns Mono.just(wine.copy(version = 0))
                val res = service.createWine(
                    NewWine("New Wine", WineType.ROSE, ExistingWinery(wineryId), ExistingRegion(regionId)), userId)
                    .suspending()
                res shouldBe wine.copy(version = 0)

                verify(exactly = 1) { transactionManager.newTx(any<Mono<WineEntity>>()) }
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
                every { wineRepository.save(wine) } returns Mono.just(wine.copy(version = 0))
                every { regionService.createRegion(newWineRegion, userId) } returns Mono.just(wineRegionEntity)
                every { regionService.createRegion(newWineryRegion, userId) } returns Mono.just(wineryRegionEntity)
                every { wineryService.createWinery(newWinery, userId) } returns Mono.just(wineryEntity)
                val res = service.createWine(
                    NewWine(
                        "New Wine",
                        WineType.ROSE,
                        NewWinery("winery", NewRegion("winery_region", "country")),
                        NewRegion("wine_region", "country")), userId).suspending()
                res shouldBe wine.copy(version = 0)

                verify(exactly = 1) { transactionManager.newTx(any<Mono<WineEntity>>()) }
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
                every { wineRepository.save(wine) } returns Mono.just(wine.copy(version = 0))
                every { regionService.createRegion(newRegion, userId) } returns Mono.just(regionEntity)
                every { wineryService.createWinery(newWinery, userId) } returns Mono.just(wineryEntity)
                val res = service.createWine(
                    NewWine(
                        "New Wine",
                        WineType.ROSE,
                        NewWinery("winery", NewRegion("region", "country")),
                        NewRegion("region", "country")), userId).suspending()
                res shouldBe wine.copy(version = 0)

                verify(exactly = 1) { transactionManager.newTx(any<Mono<WineEntity>>()) }
            }

            "fail if a reference cannot be found" {
                val wineRegionId = UUID.randomUUID()
                val wineRegion = RegionEntity(wineRegionId, 0, "wine_region", "country", userId)
                val wineryRegionId = UUID.randomUUID()
                val wineryId = UUID.randomUUID()
                val winery = WineryEntity(wineryId, 0, "winery", wineryRegionId, userId)
                every { regionService.getRegion(wineRegionId, userId) } returns Mono.empty()
                every { wineryService.getWinery(wineryId, userId) } returns Mono.just(winery)
                val exception1 = shouldThrow<ObjectNotFoundException> {
                    service.createWine(
                        NewWine(
                            "New Wine",
                            WineType.ROSE,
                            ExistingWinery(wineryId),
                            ExistingRegion(wineRegionId)), userId).suspending()
                }
                exception1 shouldHaveMessage "Wine region with id '$wineRegionId' not found for user $userId."

                every { regionService.getRegion(wineRegionId, userId) } returns Mono.just(wineRegion)
                every { wineryService.getWinery(wineryId, userId) } returns Mono.empty()
                val exception2 = shouldThrow<ObjectNotFoundException> {
                    service.createWine(
                        NewWine(
                            "New Wine",
                            WineType.ROSE,
                            ExistingWinery(wineryId),
                            ExistingRegion(wineRegionId)), userId).suspending()
                }
                exception2 shouldHaveMessage "Winery with id '$wineryId' not found."
            }
        }
    }
}