package com.amulet.cavinist.service.wine

import com.amulet.cavinist.persistence.data.wine.*
import com.amulet.cavinist.persistence.repository.wine.WineRepository
import com.amulet.cavinist.service.*
import org.springframework.stereotype.Service
import reactor.core.publisher.*
import java.util.UUID

@Service
class WineService(
    val wineRepository: WineRepository,
    val wineryService: WineryService,
    val regionService: RegionService,
    val entityFactory: WineEntityFactory,
    val txManager: TxManager) {

    fun getWine(id: UUID, userId: UUID): Mono<WineEntity> = wineRepository.findForUser(id, userId)

    fun listWines(userId: UUID): Flux<WineWithDependencies> = wineRepository.findAllForUser(userId)

    fun createWine(newWine: NewWine, userId: UUID): Mono<WineEntity> =
        txManager.newTx(createWineInternal(newWine, userId))

    private fun createWineInternal(newWine: NewWine, userId: UUID): Mono<WineEntity> {

        val winery = when (val w = newWine.winery) {
            is NewWinery      -> wineryService.createWinery(w, userId)
            is ExistingWinery -> wineryService.getWinery(w.id, userId)
                .switchIfEmpty(Mono.error(ObjectNotFoundException("Winery with id '${w.id}' not found.")))
        }

        // If both the wine and the winery regions are the same then we must ensure it is only saved once
        val saveWineRegion = when (val newWinery = newWine.winery) {
            is NewWinery -> newWinery.region != newWine.region
            else         -> true
        }

        if (saveWineRegion) {
            val wineRegion = when (val r = newWine.region) {
                is NewRegion      -> regionService.createRegion(r, userId)
                is ExistingRegion -> regionService.getRegion(r.id, userId)
                    .switchIfEmpty(Mono.error(ObjectNotFoundException("Wine region with id '${r.id}' not found for user $userId.")))
            }
            return wineRegion.flatMap { region ->
                winery.flatMap {
                    wineRepository.save(entityFactory.newWine(newWine.name, newWine.type, it.ID, region.ID, userId))
                }
            }
        } else {
            return winery.flatMap {
                wineRepository.save(entityFactory.newWine(newWine.name, newWine.type, it.ID, it.regionId, userId))
            }
        }
    }
}