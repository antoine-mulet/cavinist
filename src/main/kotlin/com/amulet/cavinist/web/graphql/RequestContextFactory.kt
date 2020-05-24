package com.amulet.cavinist.web.graphql

import com.amulet.cavinist.security.JwtUtils
import com.expediagroup.graphql.spring.execution.GraphQLContextFactory
import org.springframework.http.server.reactive.*
import org.springframework.stereotype.Component

@Component
class RequestContextFactory(val jwtUtils: JwtUtils) : GraphQLContextFactory<RequestContext> {

    override suspend fun generateContext(request: ServerHttpRequest, response: ServerHttpResponse): RequestContext {
        val bearer = request.headers.getFirst("Bearer")
        val maybeUserId = bearer?.let {
            when (val jwt = jwtUtils.decodeJwt(it)) {
                is JwtUtils.Companion.JwtDecodeResult.ValidJwt -> jwt.userId
                else                                           -> null
            }
        }
        return RequestContext(maybeUserId)
    }
}