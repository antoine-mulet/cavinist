package com.amulet.cavinist.service.wine

import com.amulet.cavinist.persistence.data.wine.*
import com.amulet.cavinist.persistence.repository.wine.RegionRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.*
import java.util.UUID

@Service
class RegionService(val repository: RegionRepository, val entityFactory: WineEntityFactory) {

    fun getRegion(id: UUID, userId: UUID): Mono<RegionEntity> = repository.findForUser(id, userId)

    fun listRegions(userId: UUID): Flux<RegionEntity> = repository.findAllForUser(userId)

    fun createRegion(newRegion: NewRegion, userId: UUID): Mono<RegionEntity> =
        repository.save(entityFactory.newRegion(newRegion.name, newRegion.country, userId))
}