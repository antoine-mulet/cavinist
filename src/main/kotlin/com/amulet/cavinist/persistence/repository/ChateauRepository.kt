package com.amulet.cavinist.persistence.repository

import com.amulet.cavinist.persistence.data.ChateauData
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChateauRepository : ReactiveCrudRepository<ChateauData, UUID>