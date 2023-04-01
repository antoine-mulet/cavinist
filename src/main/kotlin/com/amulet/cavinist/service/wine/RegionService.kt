package com.amulet.cavinist.service.wine

import com.amulet.cavinist.persistence.data.wine.RegionEntity
import com.amulet.cavinist.persistence.data.wine.WineEntityFactory
import com.amulet.cavinist.persistence.repository.wine.RegionRepository
import com.amulet.cavinist.service.ObjectNotFoundException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RegionService(val repository: RegionRepository, val entityFactory: WineEntityFactory) {

    suspend fun findRegion(id: UUID, userId: UUID): RegionEntity? = repository.findForUser(id, userId)

    suspend fun getRegion(id: UUID, userId: UUID): RegionEntity = repository.findForUser(id, userId)
        ?: throw ObjectNotFoundException("Winery region with id '$id' not found for user $userId.")

    suspend fun listRegions(userId: UUID): List<RegionEntity> = repository.findAllForUser(userId)

    suspend fun createRegion(newRegion: NewRegion, userId: UUID): RegionEntity =
        repository.save(entityFactory.newRegion(newRegion.name, newRegion.country, userId))
}