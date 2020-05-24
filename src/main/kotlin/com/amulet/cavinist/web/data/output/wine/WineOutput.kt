package com.amulet.cavinist.web.data.output.wine

import com.amulet.cavinist.persistence.data.wine.*
import com.amulet.cavinist.web.graphql.RequestContext
import com.amulet.cavinist.web.data.output.wine.resolver.*
import com.expediagroup.graphql.annotations.*
import java.util.UUID

@GraphQLName("Wine")
class WineOutput(
    val id: UUID,
    val version: Int,
    val name: String,
    val type: WineType,
    @GraphQLIgnore private val wineryResolver: WineryOutputResolver,
    @GraphQLIgnore private val regionResolver: RegionOutputResolver) {

    constructor(wineEntity: WineEntity) : this(
        wineEntity.ID, wineEntity.version(), wineEntity.name, wineEntity.type,
        ReactiveWineryOutputResolver(wineEntity.wineryId),
        ReactiveRegionOutputResolver(wineEntity.regionId))

    constructor(wineWithDependencies: WineWithDependencies) : this(
        wineWithDependencies.id,
        wineWithDependencies.version,
        wineWithDependencies.name,
        wineWithDependencies.wineType,
        CacheWineryOutputResolver(WineryOutput(wineWithDependencies.winery)),
        CacheRegionOutputResolver(RegionOutput(wineWithDependencies.region)))

    suspend fun winery(context: RequestContext): WineryOutput? = wineryResolver.winery(context)

    suspend fun region(context: RequestContext): RegionOutput? = regionResolver.region(context)
}