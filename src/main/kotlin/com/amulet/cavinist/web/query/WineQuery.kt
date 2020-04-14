package com.amulet.cavinist.web.query

import com.amulet.cavinist.service.WineService
import com.amulet.cavinist.web.data.Wine
import com.expediagroup.graphql.spring.operations.Query
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class WineQuery(val wineService: WineService) : Query {

    suspend fun getWine(id: UUID): Wine? {
        val maybeWine = wineService.getWine(id)
        return maybeWine?.let { Wine(it.id, it.name, it.year, it.chateauId) }
    }

    suspend fun listWines(): List<Wine> {
        return wineService.listWines().map { Wine(it.id, it.name, it.year, it.chateauId) }
    }
}