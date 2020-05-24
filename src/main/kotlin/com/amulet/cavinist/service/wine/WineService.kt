package com.amulet.cavinist.service.wine

import com.amulet.cavinist.persistence.data.wine.*
import com.amulet.cavinist.persistence.repository.wine.*
import com.amulet.cavinist.service.*
import com.amulet.cavinist.web.data.input.wine.*
import kotlinx.coroutines.reactive.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class WineService(
    val wineRepository: WineRepository,
    val wineryRepository: WineryRepository,
    val regionRepository: RegionRepository,
    val entityFactory: WineEntityFactory,
    val txManager: TxManager) {

    suspend fun getWine(id: UUID): WineEntity? = wineRepository.findById(id).awaitFirstOrNull()

    suspend fun listWines(): List<WineWithDependencies> =
        wineRepository.findAllWithDependencies().collectList().awaitSingle()

    suspend fun createWine(wineInput: WineInput): WineEntity? {
        return createWineWithDependencies(
            wineInput.name,
            wineInput.type,
            wineInput.wineryInput.toWineryInput(),
            wineInput.regionInput.toRegionInput()).awaitFirstOrNull()
    }

    protected fun createWineWithDependencies(
        wineName: String,
        wineType: WineType,
        wineryInput: WineryInput,
        wineRegionInput: RegionInput): Mono<WineEntity> {

        val wineRegion = when (wineRegionInput) {
            is NewRegionInput      -> regionRepository.save(
                entityFactory.newRegion(wineRegionInput.name, wineRegionInput.country))
            is ExistingRegionInput -> regionRepository.findById(wineRegionInput.id)
                .switchIfEmpty(Mono.error(ObjectNotFoundException("Wine region with id '${wineRegionInput.id}' not found.")))
        }.cache() // we need to cache the value in case it is used twice in the following code to make sure we only save once

        val winery = when (wineryInput) {
            is NewWineryInput      -> {
                val wineryRegion = when (val wineryRegionInput = wineryInput.regionInput) {
                    is NewRegionInput      ->
                        if (wineryRegionInput != wineRegionInput)
                            regionRepository.save(
                                entityFactory.newRegion(wineryRegionInput.name, wineryRegionInput.country))
                        else wineRegion // wine and winery have the same region so we only save it once
                    is ExistingRegionInput -> regionRepository.findById(wineryRegionInput.id)
                        .switchIfEmpty(Mono.error(
                            ObjectNotFoundException(
                                "Winery region with id '${wineryRegionInput.id}' not found.")))
                }
                wineryRegion.flatMap {
                    wineryRepository.save(entityFactory.newWinery(wineryInput.name, it.ID))
                }
            }
            is ExistingWineryInput -> wineryRepository.findById(wineryInput.id)
                .switchIfEmpty(Mono.error(ObjectNotFoundException("Winery with id '${wineryInput.id}' not found.")))
        }

        return txManager.newTx(
            wineRegion.flatMap { region ->
                winery.flatMap {
                    wineRepository.save(entityFactory.newWine(wineName, wineType, it.ID, region.ID))
                }
            }
                              )
    }
}