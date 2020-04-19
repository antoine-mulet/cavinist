package com.amulet.cavinist.web.data.output

import com.amulet.cavinist.persistence.data.RegionEntity
import com.expediagroup.graphql.annotations.GraphQLName
import java.util.UUID

@GraphQLName("Region")
class RegionOutput(val id: UUID, val version: Int, val name: String, val country: String) {

    constructor(regionEntity: RegionEntity) : this(
        regionEntity.id,
        regionEntity.version(),
        regionEntity.name,
        regionEntity.country)
}