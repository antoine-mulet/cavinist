package com.amulet.cavinist.web.graphql

import com.expediagroup.graphql.execution.GraphQLContext
import java.util.UUID

class RequestContext(private val maybeUserId: UUID?) : GraphQLContext {

    fun userId() = maybeUserId ?: throw InvalidOrMissingAuthenticationException("Invalid or missing JWT.")

}