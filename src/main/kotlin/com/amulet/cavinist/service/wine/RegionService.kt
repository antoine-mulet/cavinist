package com.amulet.cavinist.service.wine

import com.amulet.cavinist.persistence.data.wine.RegionEntity
import com.amulet.cavinist.persistence.repository.wine.RegionRepository
import kotlinx.coroutines.reactive.*
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RegionService(val repository: RegionRepository) {

    suspend fun getRegion(id: UUID): RegionEntity? = repository.findById(id).awaitFirstOrNull()

    suspend fun listRegions(): List<RegionEntity> = repository.findAll().collectList().awaitSingle()
}