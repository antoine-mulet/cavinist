package com.amulet.cavinist.web.query

import com.amulet.cavinist.service.wine.WineService
import com.amulet.cavinist.web.data.output.wine.WineOutput
import com.amulet.cavinist.web.graphql.RequestContext
import com.expediagroup.graphql.spring.operations.Query
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class WineQuery(private val wineService: WineService) : Query {

    suspend fun getWine(context: RequestContext, id: UUID): WineOutput? =
        wineService.findWine(id, context.userId())?.let { wine -> WineOutput(wine) }

    suspend fun listWines(context: RequestContext): List<WineOutput> =
        wineService.listWines(context.userId()).map { WineOutput(it) }
}