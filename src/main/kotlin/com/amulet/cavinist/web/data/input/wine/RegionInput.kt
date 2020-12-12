package com.amulet.cavinist.web.data.input.wine

import com.amulet.cavinist.service.wine.*
import com.amulet.cavinist.web.data.input.InvalidInputDataException
import com.expediagroup.graphql.annotations.GraphQLDescription
import java.util.UUID

@GraphQLDescription("Polymorphic type that can represent an existing region when setting the `id` attribute or a new region when setting the other attributes")
data class RegionInput(val id: UUID?, val name: String?, val country: String?) {

    fun toRegion(): Region {
        return if (id != null && name == null && country == null) ExistingRegion(id)
        else if (id == null && name != null && country != null) NewRegion(name, country)
        else throw InvalidInputDataException("`id` only is required for an existing region, `name` and `country` are required to create a new region.")
    }
}