package com.amulet.cavinist.web.data.output.resolver

import com.amulet.cavinist.web.context.ServiceContext
import com.amulet.cavinist.web.data.output.*
import com.amulet.cavinist.web.query.RegionQuery
import java.util.UUID

interface RegionOutputResolver {
    suspend fun region(context: ServiceContext): RegionOutput?
}

class ReactiveRegionOutputResolver(private val _regionId: UUID) :
    RegionOutputResolver {

    override suspend fun region(context: ServiceContext): RegionOutput? {
        return RegionQuery.getRegion(context, _regionId)
    }
}

class CacheRegionOutputResolver(private val _region: RegionOutput) :
    RegionOutputResolver {

    override suspend fun region(context: ServiceContext): RegionOutput? {
        return _region
    }
}