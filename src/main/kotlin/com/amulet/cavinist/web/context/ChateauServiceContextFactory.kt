package com.amulet.cavinist.web.context

import com.amulet.cavinist.service.ChateauService
import com.expediagroup.graphql.spring.execution.GraphQLContextFactory
import org.springframework.http.server.reactive.*
import org.springframework.stereotype.Component

@Component
class ChateauServiceContextFactory(val chateauService: ChateauService) :
    GraphQLContextFactory<ChateauServiceContext> {

    override suspend fun generateContext(
        request: ServerHttpRequest, response: ServerHttpResponse): ChateauServiceContext {
        return ChateauServiceContext(chateauService)
    }
}