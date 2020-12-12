package com.amulet.cavinist.service.wine

import java.util.UUID

sealed class Region

data class NewRegion(val name: String, val country: String) : Region()

data class ExistingRegion(val id: UUID) : Region()