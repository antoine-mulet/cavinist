package com.amulet.cavinist.web.query

import com.amulet.cavinist.web.context.ServiceContext
import com.amulet.cavinist.web.data.output.WineryOutput
import com.expediagroup.graphql.spring.operations.Query
import org.springframework.stereotype.Component
import java.util.UUID

@Component
object WineryQuery : Query {

    suspend fun getWinery(context: ServiceContext, id: UUID): WineryOutput? {
        val maybeWinery = context.wineryService.getWinery(id)
        return maybeWinery?.let { WineryOutput(it) }
    }

    suspend fun listWineries(context: ServiceContext): List<WineryOutput> {
        return context.wineryService.listWineries().map { WineryOutput(it) }
    }
}