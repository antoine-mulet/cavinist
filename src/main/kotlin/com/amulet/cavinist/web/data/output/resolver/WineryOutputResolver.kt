package com.amulet.cavinist.web.data.output.resolver

import com.amulet.cavinist.web.context.ServiceContext
import com.amulet.cavinist.web.data.output.WineryOutput
import com.amulet.cavinist.web.query.WineryQuery
import java.util.UUID

interface WineryOutputResolver {
    suspend fun winery(context: ServiceContext): WineryOutput?
}

class ReactiveWineryOutputResolver(private val _wineryId: UUID) :
    WineryOutputResolver {

    override suspend fun winery(context: ServiceContext): WineryOutput? {
        return WineryQuery.getWinery(context, _wineryId)
    }
}

class CacheWineryOutputResolver(private val _winery: WineryOutput) :
    WineryOutputResolver {

    override suspend fun winery(context: ServiceContext): WineryOutput? {
        return _winery
    }
}