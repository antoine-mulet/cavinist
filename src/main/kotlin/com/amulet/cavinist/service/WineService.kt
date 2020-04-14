package com.amulet.cavinist.service

import com.amulet.cavinist.persistence.data.WineData
import com.amulet.cavinist.persistence.repository.WineRepository
import kotlinx.coroutines.reactive.*
import org.springframework.stereotype.Service
import java.util.*

@Service
class WineService(val repo: WineRepository) {

    suspend fun getWine(id: UUID): WineData? = repo.findById(id).awaitFirstOrNull()

    suspend fun listWines(): List<WineData> = repo.findAll().collectList().awaitSingle()

    suspend fun createWine(name: String, year: Int, chateauId: UUID): WineData? {
        val wine = WineData(null, chateauId, name, year)
        return repo.save(wine).awaitFirstOrNull()
    }
}