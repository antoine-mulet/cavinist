package com.amulet.cavinist.web.data.output.wine.resolver

import com.amulet.cavinist.web.context.*
import com.amulet.cavinist.web.data.output.wine.RegionOutput
import com.amulet.cavinist.web.graphql.RequestContext
import java.util.UUID

interface RegionOutputResolver {
    suspend fun region(context: RequestContext): RegionOutput?
}

class ReactiveRegionOutputResolver(private val regionId: UUID) : RegionOutputResolver {

    override suspend fun region(context: RequestContext): RegionOutput? =
        WineQueryContext.getRegionQuery().getRegion(context, regionId)
}

class CacheRegionOutputResolver(private val region: RegionOutput) : RegionOutputResolver {

    override suspend fun region(context: RequestContext): RegionOutput? = region
}