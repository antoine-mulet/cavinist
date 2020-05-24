package com.amulet.cavinist.web.data.input.wine

import com.amulet.cavinist.web.data.input.InvalidInputDataException
import com.expediagroup.graphql.annotations.*
import java.util.UUID

@GraphQLName("RegionInput")
@GraphQLDescription("Polymorphic type that can represent an existing region when setting the `id` attribute or a new region when setting the other attributes")
data class PolymorphicRegionInput(val id: UUID?, val name: String?, val country: String?) {

    fun toRegionInput(): RegionInput {
        return if (id != null && name == null && country == null) ExistingRegionInput(
            id)
        else if (id == null && name != null && country != null) NewRegionInput(
            name,
            country)
        else throw InvalidInputDataException("`id` only is required for an existing region, `name` and `country` are required to create a new region.")
    }
}

sealed class RegionInput

data class NewRegionInput(val name: String, val country: String) : RegionInput()

data class ExistingRegionInput(val id: UUID) : RegionInput()