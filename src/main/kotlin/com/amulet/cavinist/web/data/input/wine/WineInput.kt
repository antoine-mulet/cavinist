package com.amulet.cavinist.web.data.input.wine

import com.amulet.cavinist.persistence.data.wine.WineType
import com.amulet.cavinist.service.wine.NewWine

data class WineInput(
    val name: String,
    val type: WineType,
    val wineryInput: WineryInput,
    val regionInput: RegionInput) {

    fun toNewWine() = NewWine(name, type, wineryInput.toWinery(), regionInput.toRegion())
}