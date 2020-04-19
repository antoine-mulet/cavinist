package com.amulet.cavinist.web.data.input

import com.amulet.cavinist.persistence.data.WineType

data class WineInput(
    val name: String,
    val type: WineType,
    val wineryInput: PolymorphicWineryInput,
    val regionInput: PolymorphicRegionInput)