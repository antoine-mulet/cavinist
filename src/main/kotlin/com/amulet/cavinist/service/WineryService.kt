package com.amulet.cavinist.service

import com.amulet.cavinist.persistence.data.*
import com.amulet.cavinist.persistence.repository.WineryRepository
import kotlinx.coroutines.reactive.*
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class WineryService(val repo: WineryRepository) {

    suspend fun getWinery(id: UUID): WineryEntity? = repo.findById(id).awaitFirstOrNull()

    suspend fun listWineries(): List<WineryWithDependencies> =
        repo.findAllWithDependencies().collectList().awaitSingle()
}