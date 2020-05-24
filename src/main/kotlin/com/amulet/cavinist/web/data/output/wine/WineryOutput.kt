package com.amulet.cavinist.web.data.output.wine

import com.amulet.cavinist.persistence.data.wine.*
import com.amulet.cavinist.web.graphql.RequestContext
import com.amulet.cavinist.web.data.output.wine.resolver.*
import com.expediagroup.graphql.annotations.*
import java.util.UUID

@GraphQLName("Winery")
class WineryOutput(
    val id: UUID,
    val version: Int,
    val name: String,
    @GraphQLIgnore val regionResolver: RegionOutputResolver) {

    constructor(wineryEntity: WineryEntity) : this(
        wineryEntity.ID,
        wineryEntity.version(),
        wineryEntity.name,
        ReactiveRegionOutputResolver(wineryEntity.regionId))

    constructor(wineryWithDependencies: WineryWithDependencies) : this(
        wineryWithDependencies.id,
        wineryWithDependencies.version,
        wineryWithDependencies.name,
        CacheRegionOutputResolver(RegionOutput(wineryWithDependencies.regionEntity)))

    suspend fun region(context: RequestContext): RegionOutput? = regionResolver.region(context)
}