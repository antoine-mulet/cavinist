package com.amulet.cavinist.persistence.repository.wine

import com.amulet.cavinist.persistence.data.wine.RegionEntity
import com.amulet.cavinist.persistence.repository.CrudRepository
import com.amulet.cavinist.persistence.repository.wine.crud.CrudRegionRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.*
import java.util.UUID

@Repository
class RegionRepository(override val crudRepository: CrudRegionRepository) : CrudRepository<RegionEntity>() {

    fun findAllForUser(userId: UUID): Flux<RegionEntity> = crudRepository.findAllByUserId(userId)

    fun findForUser(id: UUID, userId: UUID): Mono<RegionEntity> = crudRepository.findByIDAndUserId(id, userId)
}