package com.amulet.cavinist.web.context

import com.amulet.cavinist.service.*
import com.expediagroup.graphql.spring.execution.GraphQLContextFactory
import org.springframework.http.server.reactive.*
import org.springframework.stereotype.Component

@Component
class ServiceContextFactory(
    val wineService: WineService,
    val wineryService: WineryService,
    val regionService: RegionService) :
    GraphQLContextFactory<ServiceContext> {

    override suspend fun generateContext(
        request: ServerHttpRequest, response: ServerHttpResponse): ServiceContext {
        return ServiceContext(wineService, wineryService, regionService)
    }
}