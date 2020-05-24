package com.amulet.cavinist.web.data.output.wine.resolver

import com.amulet.cavinist.web.context.*
import com.amulet.cavinist.web.data.output.wine.WineryOutput
import com.amulet.cavinist.web.graphql.RequestContext
import java.util.UUID

interface WineryOutputResolver {
    suspend fun winery(context: RequestContext): WineryOutput?
}

class ReactiveWineryOutputResolver(private val wineryId: UUID) : WineryOutputResolver {

    override suspend fun winery(context: RequestContext): WineryOutput? =
        WineQueryContext.getWineryQuery().getWinery(context, wineryId)
}

class CacheWineryOutputResolver(private val winery: WineryOutput) : WineryOutputResolver {

    override suspend fun winery(context: RequestContext): WineryOutput? = winery
}