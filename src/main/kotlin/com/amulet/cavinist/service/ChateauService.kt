package com.amulet.cavinist.service

import com.amulet.cavinist.persistence.data.ChateauData
import com.amulet.cavinist.persistence.repository.ChateauRepository
import kotlinx.coroutines.reactive.*
import org.springframework.stereotype.Service
import java.util.*

@Service
class ChateauService(val repo: ChateauRepository) {

    suspend fun getChateau(id: UUID): ChateauData? = repo.findById(id).awaitFirstOrNull()

    suspend fun listChateaux(): List<ChateauData> = repo.findAll().collectList().awaitSingle()

    suspend fun createChateau(name: String, region: String): ChateauData? {
        val chateau = ChateauData(null, name, region)
        return repo.save(chateau).awaitFirstOrNull()
    }
}