package com.amulet.cavinist.persistence.repository.crud

import com.amulet.cavinist.persistence.data.WineryEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CrudWineryRepository: ReactiveCrudRepository<WineryEntity, UUID>