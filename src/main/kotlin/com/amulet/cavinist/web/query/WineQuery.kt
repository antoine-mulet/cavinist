package com.amulet.cavinist.web.query

import com.amulet.cavinist.service.wine.WineService
import com.amulet.cavinist.utils.suspending
import com.amulet.cavinist.web.data.output.wine.WineOutput
import com.amulet.cavinist.web.graphql.RequestContext
import com.expediagroup.graphql.spring.operations.Query
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class WineQuery(private val wineService: WineService) : Query {

    suspend fun getWine(context: RequestContext, id: UUID): WineOutput? =
        wineService.getWine(id, context.userId()).map { wine -> WineOutput(wine) }.suspending()

    suspend fun listWines(context: RequestContext): List<WineOutput> =
        wineService.listWines(context.userId()).map { WineOutput(it) }.suspending()
}