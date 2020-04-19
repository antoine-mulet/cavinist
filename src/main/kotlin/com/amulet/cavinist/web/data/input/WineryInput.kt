package com.amulet.cavinist.web.data.input

import com.expediagroup.graphql.annotations.*
import java.util.UUID

@GraphQLName("WineryInput")
@GraphQLDescription("Polymorphic type that can represent an existing winery when setting the `id` attribute or a new winery when setting the other attributes")
data class PolymorphicWineryInput(
    val id: UUID?,
    val name: String?,
    val regionInput: PolymorphicRegionInput?) {

    fun toWineryInput(): WineryInput {
        return if (id != null && name == null && regionInput == null)
            ExistingWineryInput(id)
        else if (id == null && name != null && regionInput != null)
            NewWineryInput(name, regionInput.toRegionInput())
        else throw InvalidInputDataException("`id` only is required for an existing winery, `name` and `regionInput` are required to create a new winery.")
    }
}

sealed class WineryInput

data class NewWineryInput(val name: String, val regionInput: RegionInput) : WineryInput()

data class ExistingWineryInput(val id: UUID) : WineryInput()