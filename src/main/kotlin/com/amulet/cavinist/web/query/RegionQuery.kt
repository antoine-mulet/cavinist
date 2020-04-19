package com.amulet.cavinist.web.query

import com.amulet.cavinist.web.context.ServiceContext
import com.amulet.cavinist.web.data.output.RegionOutput
import com.expediagroup.graphql.spring.operations.Query
import org.springframework.stereotype.Component
import java.util.UUID

@Component
object RegionQuery : Query {

    suspend fun getRegion(context: ServiceContext, id: UUID): RegionOutput? {
        val maybeRegion = context.regionService.getRegion(id)
        return maybeRegion?.let { RegionOutput(it) }
    }

    suspend fun listRegions(context: ServiceContext): List<RegionOutput> {
        return context.regionService.listRegions().map { RegionOutput(it) }
    }
}