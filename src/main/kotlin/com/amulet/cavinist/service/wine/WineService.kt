package com.amulet.cavinist.service.wine

import com.amulet.cavinist.persistence.data.wine.WineEntity
import com.amulet.cavinist.persistence.data.wine.WineEntityFactory
import com.amulet.cavinist.persistence.data.wine.WineWithDependencies
import com.amulet.cavinist.persistence.repository.wine.WineRepository
import com.amulet.cavinist.service.TxManager
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class WineService(
    val wineRepository: WineRepository,
    val wineryService: WineryService,
    val regionService: RegionService,
    val entityFactory: WineEntityFactory,
    val txManager: TxManager
) {

    suspend fun findWine(id: UUID, userId: UUID): WineEntity? = wineRepository.findForUser(id, userId)

    suspend fun listWines(userId: UUID): List<WineWithDependencies> = wineRepository.findAllForUser(userId)

    suspend fun createWine(newWine: NewWine, userId: UUID): WineEntity =
        txManager.newTx { createWineInternal(newWine, userId) }

    private suspend fun createWineInternal(newWine: NewWine, userId: UUID): WineEntity {

        val winery = when (val w = newWine.winery) {
            is NewWinery -> wineryService.createWinery(w, userId)
            is ExistingWinery -> wineryService.getWinery(w.id, userId)
        }

        // If both the wine and the winery regions are the same then we must ensure it is only saved once
        val saveWineRegion = when (val newWinery = newWine.winery) {
            is NewWinery -> newWinery.region != newWine.region
            else -> true
        }

        if (saveWineRegion) {
            val wineRegion = when (val r = newWine.region) {
                is NewRegion -> regionService.createRegion(r, userId)
                is ExistingRegion -> regionService.getRegion(r.id, userId)
            }
            return wineRepository.save(
                entityFactory.newWine(
                    newWine.name,
                    newWine.type,
                    winery.ID,
                    wineRegion.ID,
                    userId
                )
            )
        } else {
            return wineRepository.save(
                entityFactory.newWine(
                    newWine.name,
                    newWine.type,
                    winery.ID,
                    winery.regionId,
                    userId
                )
            )
        }
    }
}