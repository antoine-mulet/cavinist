package com.amulet.cavinist.persistence.repository.wine.crud

import com.amulet.cavinist.persistence.data.wine.WineryEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CrudWineryRepository : CoroutineCrudRepository<WineryEntity, UUID> {

    suspend fun findByIdAndUserId(id: UUID, userId: UUID): WineryEntity?

}