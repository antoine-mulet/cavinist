package com.amulet.cavinist.service.wine

import com.amulet.cavinist.persistence.data.wine.WineType
import java.util.UUID

sealed class Wine

data class NewWine(val name: String, val type: WineType, val winery: Winery, val region: Region) : Wine()