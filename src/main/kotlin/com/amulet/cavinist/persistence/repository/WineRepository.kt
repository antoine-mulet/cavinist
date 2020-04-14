package com.amulet.cavinist.persistence.repository

import com.amulet.cavinist.persistence.data.WineData
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WineRepository : ReactiveCrudRepository<WineData, UUID>