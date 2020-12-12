package com.amulet.cavinist.persistence.repository.wine.crud

import com.amulet.cavinist.persistence.data.wine.RegionEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.*
import java.util.UUID

@Repository
interface CrudRegionRepository : ReactiveCrudRepository<RegionEntity, UUID> {

    fun findAllByUserId(userId: UUID): Flux<RegionEntity>

    fun findByIDAndUserId(id: UUID, userId: UUID): Mono<RegionEntity>
}