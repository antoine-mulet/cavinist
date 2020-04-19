package com.amulet.cavinist.web.data.output

import com.amulet.cavinist.persistence.data.*
import com.amulet.cavinist.web.context.ServiceContext
import com.amulet.cavinist.web.data.output.resolver.*
import com.expediagroup.graphql.annotations.*
import java.util.UUID

@GraphQLName("Winery")
class WineryOutput(
    val id: UUID,
    val version: Int,
    val name: String,
    @GraphQLIgnore val regionResolver: RegionOutputResolver) {

    constructor(wineryEntity: WineryEntity) : this(
        wineryEntity.id,
        wineryEntity.version(),
        wineryEntity.name,
        ReactiveRegionOutputResolver(wineryEntity.regionId))

    constructor(wineryWithDependencies: WineryWithDependencies) : this(
        wineryWithDependencies.id,
        wineryWithDependencies.version,
        wineryWithDependencies.name,
        CacheRegionOutputResolver(RegionOutput(wineryWithDependencies.regionEntity)))

    suspend fun region(context: ServiceContext): RegionOutput? {
        return regionResolver.region(context)
    }
}