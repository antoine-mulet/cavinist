package com.amulet.cavinist.web.query

import com.amulet.cavinist.service.wine.WineService
import com.amulet.cavinist.web.graphql.RequestContext
import com.amulet.cavinist.web.data.output.wine.WineOutput
import com.expediagroup.graphql.spring.operations.Query
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class WineQuery(private val wineService: WineService) : Query {

    suspend fun getWine(context: RequestContext, id: UUID): WineOutput? = wineService.getWine(id)?.let { WineOutput(it) }

    suspend fun listWines(context: RequestContext): List<WineOutput> {
        println("User id: ${context.userId}")
        return wineService.listWines().map { WineOutput(it) }
    }
}