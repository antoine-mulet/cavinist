package com.amulet.cavinist.web.query

import com.amulet.cavinist.service.wine.WineryService
import com.amulet.cavinist.utils.suspending
import com.amulet.cavinist.web.data.output.wine.WineryOutput
import com.amulet.cavinist.web.graphql.RequestContext
import com.expediagroup.graphql.spring.operations.Query
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class WineryQuery(private val wineryService: WineryService) : Query {

    suspend fun getWinery(context: RequestContext, id: UUID): WineryOutput? =
        wineryService.getWinery(id, context.userId()).map { winery -> WineryOutput(winery) }.suspending()

    suspend fun listWineries(context: RequestContext): List<WineryOutput> =
        wineryService.listWineries(context.userId()).map { WineryOutput(it) }.suspending()
}