package com.amulet.cavinist.persistence.data

import java.util.UUID

data class WineryWithDependencies(val id: UUID, val version: Int, val name: String, val regionEntity: RegionEntity)