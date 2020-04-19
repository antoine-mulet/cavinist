package com.amulet.cavinist.web.query

import com.amulet.cavinist.web.context.ServiceContext
import com.amulet.cavinist.web.data.output.WineOutput
import com.expediagroup.graphql.spring.operations.Query
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import java.util.UUID

@Component
object WineQuery : Query {

    suspend fun getWine(context: ServiceContext, id: UUID): WineOutput? {
        val maybeWine = context.wineService.getWine(id)
        return maybeWine?.let { WineOutput(it) }
    }

    suspend fun listWines(context: ServiceContext): List<WineOutput> {
        return context.wineService.listWines().map { WineOutput(it) }
    }
}