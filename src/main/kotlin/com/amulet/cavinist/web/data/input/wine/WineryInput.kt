package com.amulet.cavinist.web.data.input.wine

import com.amulet.cavinist.service.wine.*
import com.amulet.cavinist.web.data.input.InvalidInputDataException
import com.expediagroup.graphql.annotations.GraphQLDescription
import java.util.UUID

@GraphQLDescription("Polymorphic type that can represent an existing winery when setting the `id` attribute or a new winery when setting the other attributes")
data class WineryInput(val id: UUID?, val name: String?, val regionInput: RegionInput?) {

    fun toWinery(): Winery {
        return if (id != null && name == null && regionInput == null)
            ExistingWinery(id)
        else if (id == null && name != null && regionInput != null)
            NewWinery(name, regionInput.toRegion())
        else throw InvalidInputDataException("`id` only is required for an existing winery, `name` and `regionInput` are required to create a new winery.")
    }
}