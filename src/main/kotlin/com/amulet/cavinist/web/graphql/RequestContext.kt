package com.amulet.cavinist.web.graphql

import com.expediagroup.graphql.execution.GraphQLContext
import java.util.UUID

class RequestContext(val userId: UUID?) : GraphQLContext