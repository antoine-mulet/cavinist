package com.amulet.cavinist.web.data.input.wine

import com.amulet.cavinist.persistence.data.wine.WineType

data class WineInput(
    val name: String,
    val type: WineType,
    val wineryInput: PolymorphicWineryInput,
    val regionInput: PolymorphicRegionInput)