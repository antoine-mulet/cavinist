package com.amulet.cavinist.web.context

import com.amulet.cavinist.service.ChateauService
import com.expediagroup.graphql.execution.GraphQLContext
import org.springframework.stereotype.Component

@Component
class ChateauServiceContext(val chateauService: ChateauService) : GraphQLContext