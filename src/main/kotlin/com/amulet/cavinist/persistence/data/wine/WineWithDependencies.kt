package com.amulet.cavinist.persistence.data.wine

import java.util.UUID

data class WineWithDependencies(
    val id: UUID,
    val version: Int,
    val name: String,
    val wineType: WineType,
    val winery: WineryWithDependencies,
    val region: RegionEntity)