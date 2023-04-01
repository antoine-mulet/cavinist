package com.amulet.cavinist.service.wine

import com.amulet.cavinist.persistence.data.wine.WineEntityFactory
import com.amulet.cavinist.persistence.data.wine.WineryEntity
import com.amulet.cavinist.persistence.data.wine.WineryWithDependencies
import com.amulet.cavinist.persistence.repository.wine.WineryRepository
import com.amulet.cavinist.service.ObjectNotFoundException
import com.amulet.cavinist.service.TxManager
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class WineryService(
    val repository: WineryRepository,
    val regionService: RegionService,
    val entityFactory: WineEntityFactory,
    val txManager: TxManager
) {

    suspend fun findWinery(id: UUID, userId: UUID): WineryEntity? = repository.findForUser(id, userId)

    suspend fun getWinery(id: UUID, userId: UUID): WineryEntity =
        repository.findForUser(id, userId) ?: throw ObjectNotFoundException("Winery with id '$id' not found.")

    suspend fun listWineries(userId: UUID): List<WineryWithDependencies> = repository.findAllForUser(userId)

    suspend fun createWinery(newWinery: NewWinery, userId: UUID): WineryEntity =
        txManager.newTx { createWineryInternal(newWinery, userId) }

    private suspend fun createWineryInternal(newWinery: NewWinery, userId: UUID): WineryEntity {
        val wineryRegion = when (val r = newWinery.region) {
            is NewRegion -> regionService.createRegion(r, userId)
            is ExistingRegion -> regionService.getRegion(r.id, userId)
        }
        return repository.save(entityFactory.newWinery(newWinery.name, wineryRegion.ID, userId))
    }
}