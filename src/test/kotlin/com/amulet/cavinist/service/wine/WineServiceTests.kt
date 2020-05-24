package com.amulet.cavinist.service.wine

import com.amulet.cavinist.common.WordSpecUnitTests
import com.amulet.cavinist.persistence.data.wine.*
import com.amulet.cavinist.persistence.repository.wine.*
import com.amulet.cavinist.service.*
import com.amulet.cavinist.web.data.input.InvalidInputDataException
import com.amulet.cavinist.web.data.input.wine.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.*
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.*
import reactor.core.publisher.*
import java.util.UUID

class WineServiceTests : WordSpecUnitTests() {

    private val wineRepository = mockk<WineRepository>()
    private val wineryRepository = mockk<WineryRepository>()
    private val regionRepository = mockk<RegionRepository>()
    private val entityFactory = mockk<WineEntityFactory>()
    private val transactionManager = mockk<TxManager>()
    private val service =
        WineService(wineRepository, wineryRepository, regionRepository, entityFactory, transactionManager)

    init {

        every { transactionManager.newTx(any<Mono<WineEntity>>()) } answers { firstArg() }

        "getWine" should {

            val id = UUID.randomUUID()

            "return the correct wine from the repository" {
                val expected = WineEntity(id, 0, "wine", WineType.WHITE, UUID.randomUUID(), UUID.randomUUID())
                every { wineRepository.findById(id) } returns Mono.just(expected)
                service.getWine(id) shouldBe expected
            }

            "return null when the wine cannot be found in the repository" {
                every { wineRepository.findById(id) } returns Mono.empty()
                service.getWine(id) should beNull()
            }
        }

        "listWines" should {

            "return all the existing wines from the repository" {
                val regionId = UUID.randomUUID()
                val regionEntity = RegionEntity(regionId, 0, "region", "country")
                val wineryId = UUID.randomUUID()
                val wineryEntity = WineryEntity(wineryId, 0, "winery", regionId)
                val wineryWithDependencies =
                    WineryWithDependencies(wineryEntity.ID, 0, wineryEntity.name, regionEntity)
                val wineEntity1 = WineEntity(UUID.randomUUID(), 1, "wine", WineType.WHITE, wineryId, regionId)
                val wineEntity2 = WineEntity(UUID.randomUUID(), 2, "wine", WineType.WHITE, wineryId, regionId)
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
                every { wineRepository.findAllWithDependencies() } returns Flux.fromIterable(expectedList)
                service.listWines() shouldBe expectedList
            }

            "return an empty list when no wine can be found in the repository" {
                every { wineRepository.findAllWithDependencies() } returns Flux.empty()
                service.listWines() shouldBe emptyList()
            }
        }

        "createWine" should {

            "save a new wine referencing existing winery and region" {
                val regionId = UUID.randomUUID()
                val regionEntity = RegionEntity(regionId, 0, "region", "country")
                every { regionRepository.findById(regionId) } returns Mono.just(regionEntity)
                val wineryId = UUID.randomUUID()
                val wineryEntity = WineryEntity(wineryId, 0, "winery", regionId)
                every { wineryRepository.findById(wineryId) } returns Mono.just(wineryEntity)
                val wineId = UUID.randomUUID()
                val wine = WineEntity(wineId, null, "New Wine", WineType.ROSE, wineryId, regionId)
                every { entityFactory.newWine("New Wine", WineType.ROSE, wineryId, regionId) } returns wine
                every { wineRepository.save(wine) } returns Mono.just(wine.copy(version = 0))
                val res = service.createWine(
                    WineInput(
                        "New Wine",
                        WineType.ROSE,
                        PolymorphicWineryInput(wineryId, null, null),
                        PolymorphicRegionInput(regionId, null, null)))
                res shouldBe wine.copy(version = 0)

                verify(exactly = 1) { transactionManager.newTx(any<Mono<WineEntity>>()) }
            }

            "save a new wine along with its winery and region" {
                val wineRegionId = UUID.randomUUID()
                val wineRegion = RegionEntity(wineRegionId, null, "wine_region", "country")
                every { entityFactory.newRegion("wine_region", "country") } returns wineRegion
                val wineryRegionId = UUID.randomUUID()
                val wineryRegion = RegionEntity(wineryRegionId, null, "winery_region", "country")
                every { entityFactory.newRegion("winery_region", "country") } returns wineryRegion
                val wineryId = UUID.randomUUID()
                val winery = WineryEntity(wineryId, null, "winery", wineryRegionId)
                every { entityFactory.newWinery("winery", wineryRegionId) } returns winery
                val wineId = UUID.randomUUID()
                val wine = WineEntity(wineId, null, "New Wine", WineType.ROSE, wineryId, wineRegionId)
                every { entityFactory.newWine("New Wine", WineType.ROSE, wineryId, wineRegionId) } returns wine
                every { wineRepository.save(wine) } returns Mono.just(wine.copy(version = 0))
                every { regionRepository.save(wineRegion) } returns Mono.just(wineRegion.copy(version = 0))
                every { regionRepository.save(wineryRegion) } returns Mono.just(wineryRegion.copy(version = 0))
                every { wineryRepository.save(winery) } returns Mono.just(winery.copy(version = 0))
                val res = service.createWine(
                    WineInput(
                        "New Wine",
                        WineType.ROSE,
                        PolymorphicWineryInput(
                            null,
                            "winery",
                            PolymorphicRegionInput(null, "winery_region", "country")),
                        PolymorphicRegionInput(null, "wine_region", "country")))
                res shouldBe wine.copy(version = 0)

                verify(exactly = 1) { transactionManager.newTx(any<Mono<WineEntity>>()) }
            }

            "save a new wine along with its winery and region when the wine and winery regions are the same" {
                val regionId = UUID.randomUUID()
                val region = RegionEntity(regionId, null, "region", "country")
                every { entityFactory.newRegion("region", "country") } returns region
                val wineryId = UUID.randomUUID()
                val winery = WineryEntity(wineryId, null, "winery", regionId)
                every { entityFactory.newWinery("winery", regionId) } returns winery
                val wineId = UUID.randomUUID()
                val wine = WineEntity(wineId, null, "New Wine", WineType.ROSE, wineryId, regionId)
                every { entityFactory.newWine("New Wine", WineType.ROSE, wineryId, regionId) } returns wine
                every { wineRepository.save(wine) } returns Mono.just(wine.copy(version = 0))
                every { regionRepository.save(region) } returns Mono.just(region.copy(version = 0))
                every { wineryRepository.save(winery) } returns Mono.just(winery.copy(version = 0))
                val res = service.createWine(
                    WineInput(
                        "New Wine",
                        WineType.ROSE,
                        PolymorphicWineryInput(null, "winery", PolymorphicRegionInput(null, "region", "country")),
                        PolymorphicRegionInput(null, "region", "country")))
                res shouldBe wine.copy(version = 0)

                verify(exactly = 1) { transactionManager.newTx(any<Mono<WineEntity>>()) }
            }

            "fail if a reference cannot be found" {
                val wineRegionId = UUID.randomUUID()
                val wineRegion = RegionEntity(wineRegionId, 0, "wine_region", "country")
                val wineryRegionId = UUID.randomUUID()
                val wineryId = UUID.randomUUID()
                val winery = WineryEntity(wineryId, 0, "winery", wineryRegionId)

                every { regionRepository.findById(wineRegionId) } returns Mono.empty()
                every { wineryRepository.findById(wineryId) } returns Mono.just(winery)
                val exception1 = shouldThrow<ObjectNotFoundException> {
                    service.createWine(
                        WineInput(
                            "New Wine",
                            WineType.ROSE,
                            PolymorphicWineryInput(wineryId, null, null),
                            PolymorphicRegionInput(wineRegionId, null, null)))
                }
                exception1 shouldHaveMessage "Wine region with id '$wineRegionId' not found."

                every { regionRepository.findById(wineRegionId) } returns Mono.just(wineRegion)
                every { wineryRepository.findById(wineryId) } returns Mono.empty()
                val exception2 = shouldThrow<ObjectNotFoundException> {
                    service.createWine(
                        WineInput(
                            "New Wine",
                            WineType.ROSE,
                            PolymorphicWineryInput(wineryId, null, null),
                            PolymorphicRegionInput(wineRegionId, null, null)))
                }
                exception2 shouldHaveMessage "Winery with id '$wineryId' not found."

                every { regionRepository.findById(wineRegionId) } returns Mono.just(wineRegion)
                every { regionRepository.findById(wineryRegionId) } returns Mono.empty()
                val exception3 = shouldThrow<ObjectNotFoundException> {
                    service.createWine(
                        WineInput(
                            "New Wine",
                            WineType.ROSE,
                            PolymorphicWineryInput(null, "winery", PolymorphicRegionInput(wineryRegionId, null, null)),
                            PolymorphicRegionInput(wineRegionId, null, null)))
                }
                exception3 shouldHaveMessage "Winery region with id '$wineryRegionId' not found."

                verify(exactly = 3) { transactionManager.newTx(any<Mono<WineEntity>>()) }
            }

            "not accept incorrect polymorphic inputs" {

                val regionExpectedMessage =
                    "`id` only is required for an existing region, `name` and `country` are required to create a new region."
                val wineryExpectedMessage =
                    "`id` only is required for an existing winery, `name` and `regionInput` are required to create a new winery."

                val exception1 = shouldThrow<InvalidInputDataException> {
                    service.createWine(
                        WineInput(
                            "New Wine",
                            WineType.WHITE,
                            PolymorphicWineryInput(UUID.randomUUID(), "test", null),
                            PolymorphicRegionInput(UUID.randomUUID(), null, null)))
                }
                exception1 shouldHaveMessage wineryExpectedMessage

                val exception2 = shouldThrow<InvalidInputDataException> {
                    service.createWine(
                        WineInput(
                            "New Wine",
                            WineType.WHITE,
                            PolymorphicWineryInput(null, null, PolymorphicRegionInput(UUID.randomUUID(), null, null)),
                            PolymorphicRegionInput(UUID.randomUUID(), null, null)))
                }
                exception2 shouldHaveMessage wineryExpectedMessage

                val exception3 = shouldThrow<InvalidInputDataException> {
                    service.createWine(
                        WineInput(
                            "New Wine",
                            WineType.WHITE,
                            PolymorphicWineryInput(UUID.randomUUID(), null, null),
                            PolymorphicRegionInput(UUID.randomUUID(), null, "test")))
                }
                exception3 shouldHaveMessage regionExpectedMessage

                val exception4 = shouldThrow<InvalidInputDataException> {
                    service.createWine(
                        WineInput(
                            "New Wine",
                            WineType.WHITE,
                            PolymorphicWineryInput(UUID.randomUUID(), null, null),
                            PolymorphicRegionInput(null, "test", null)))
                }
                exception4 shouldHaveMessage regionExpectedMessage

                val exception5 = shouldThrow<InvalidInputDataException> {
                    service.createWine(
                        WineInput(
                            "New Wine",
                            WineType.WHITE,
                            PolymorphicWineryInput(null, "test", PolymorphicRegionInput(null, "test", null)),
                            PolymorphicRegionInput(null, "test", "test")))
                }
                exception5 shouldHaveMessage regionExpectedMessage

                verify(exactly = 0) { transactionManager.newTx(any<Mono<WineEntity>>()) }
            }
        }
    }
}