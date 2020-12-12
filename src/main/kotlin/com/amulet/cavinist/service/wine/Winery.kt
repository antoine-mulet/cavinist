package com.amulet.cavinist.service.wine

import java.util.UUID

sealed class Winery

data class NewWinery(val name: String, val region: Region) : Winery()

data class ExistingWinery(val id: UUID) : Winery()