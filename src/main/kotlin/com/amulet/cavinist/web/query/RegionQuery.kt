package com.amulet.cavinist.web.query

import com.amulet.cavinist.service.wine.RegionService
import com.amulet.cavinist.utils.suspending
import com.amulet.cavinist.web.graphql.RequestContext
import com.amulet.cavinist.web.data.output.wine.RegionOutput
import com.expediagroup.graphql.spring.operations.Query
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class RegionQuery(private val regionService: RegionService) : Query {

    suspend fun getRegion(context: RequestContext, id: UUID): RegionOutput? =
        regionService.getRegion(id, context.userId()).map { region -> RegionOutput(region) }.suspending()

    suspend fun listRegions(context: RequestContext): List<RegionOutput> =
        regionService.listRegions(context.userId()).map { RegionOutput(it) }.suspending()
}