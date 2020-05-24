package com.amulet.cavinist.persistence.repository.wine

import com.amulet.cavinist.persistence.data.wine.RegionEntity
import com.amulet.cavinist.persistence.repository.CrudRepository
import com.amulet.cavinist.persistence.repository.wine.crud.CrudRegionRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class RegionRepository(override val crudRepository: CrudRegionRepository) : CrudRepository<RegionEntity>() {

    fun findAll(): Flux<RegionEntity> = crudRepository.findAll()
}