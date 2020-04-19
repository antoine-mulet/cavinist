package com.amulet.cavinist.persistence.repository

import com.amulet.cavinist.persistence.data.RegionEntity
import com.amulet.cavinist.persistence.repository.crud.CrudRegionRepository
import org.springframework.stereotype.Repository

@Repository
class RegionRepository(override val crudRepository: CrudRegionRepository) : CrudRepository<RegionEntity>()