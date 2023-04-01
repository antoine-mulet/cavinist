package com.amulet.cavinist.persistence.repository.wine

import com.amulet.cavinist.persistence.data.wine.RegionEntity
import com.amulet.cavinist.persistence.repository.CrudCoroutineRepository
import com.amulet.cavinist.persistence.repository.wine.crud.CrudRegionRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class RegionRepository(override val crudRepository: CrudRegionRepository) : CrudCoroutineRepository<RegionEntity>() {

    suspend fun findAllForUser(userId: UUID): List<RegionEntity> = crudRepository.findAllByUserId(userId)

    suspend fun findForUser(id: UUID, userId: UUID): RegionEntity? = crudRepository.findByIDAndUserId(id, userId)
}