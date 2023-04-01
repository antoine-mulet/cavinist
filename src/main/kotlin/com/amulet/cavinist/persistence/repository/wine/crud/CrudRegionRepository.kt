package com.amulet.cavinist.persistence.repository.wine.crud

import com.amulet.cavinist.persistence.data.wine.RegionEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CrudRegionRepository : CoroutineCrudRepository<RegionEntity, UUID> {

    suspend fun findAllByUserId(userId: UUID): List<RegionEntity>

    suspend fun findByIDAndUserId(id: UUID, userId: UUID): RegionEntity?
}