package com.amulet.cavinist.service.wine

import com.amulet.cavinist.persistence.data.wine.*
import com.amulet.cavinist.persistence.repository.wine.WineryRepository
import com.amulet.cavinist.service.*
import org.springframework.stereotype.Service
import reactor.core.publisher.*
import java.util.UUID

@Service
class WineryService(
    val repository: WineryRepository,
    val regionService: RegionService,
    val entityFactory: WineEntityFactory,
    val txManager: TxManager) {

    fun getWinery(id: UUID, userId: UUID): Mono<WineryEntity> = repository.findForUser(id, userId)

    fun listWineries(userId: UUID): Flux<WineryWithDependencies> = repository.findAllForUser(userId)

    fun createWinery(newWinery: NewWinery, userId: UUID): Mono<WineryEntity> =
        txManager.newTx(createWineryInternal(newWinery, userId))

    private fun createWineryInternal(newWinery: NewWinery, userId: UUID): Mono<WineryEntity> {
        val wineryRegion = when (val r = newWinery.region) {
            is NewRegion      -> regionService.createRegion(r, userId)
            is ExistingRegion -> regionService.getRegion(r.id, userId)
                .switchIfEmpty(Mono.error(ObjectNotFoundException("Winery region with id '${r.id}' not found for user $userId.")))
        }
        return wineryRegion.flatMap {
            repository.save(entityFactory.newWinery(newWinery.name, it.ID, userId))
        }
    }
}