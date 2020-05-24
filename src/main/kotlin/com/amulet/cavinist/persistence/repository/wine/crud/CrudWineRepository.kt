package com.amulet.cavinist.persistence.repository.wine.crud

import com.amulet.cavinist.persistence.data.wine.WineEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CrudWineRepository : ReactiveCrudRepository<WineEntity, UUID>