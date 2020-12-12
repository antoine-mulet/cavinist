package com.amulet.cavinist.persistence.repository.wine.crud

import com.amulet.cavinist.persistence.data.wine.WineryEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
interface CrudWineryRepository: ReactiveCrudRepository<WineryEntity, UUID> {

    fun findByIdAndUserId(id: UUID, userId: UUID): Mono<WineryEntity>

}