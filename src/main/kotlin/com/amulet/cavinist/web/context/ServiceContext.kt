package com.amulet.cavinist.web.context

import com.amulet.cavinist.service.*
import com.expediagroup.graphql.execution.GraphQLContext
import org.springframework.stereotype.Component

@Component
class ServiceContext(val wineService: WineService, val wineryService: WineryService, val regionService: RegionService) :
    GraphQLContext