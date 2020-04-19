package com.amulet.cavinist.persistence.data

import org.springframework.stereotype.Component
import java.util.UUID

interface EntityFactory {

    fun newRegion(name: String, country: String): RegionEntity

    fun newWinery(name: String, regionId: UUID): WineryEntity

    fun newWine(name: String, type: WineType, wineryId: UUID, regionId: UUID): WineEntity
}

@Component
object EntityFactoryImpl : EntityFactory {

    override fun newRegion(name: String, country: String): RegionEntity =
        RegionEntity(UUID.randomUUID(), null, name, country)

    override fun newWinery(name: String, regionId: UUID): WineryEntity =
        WineryEntity(UUID.randomUUID(), null, name, regionId)

    override fun newWine(name: String, type: WineType, wineryId: UUID, regionId: UUID): WineEntity =
        WineEntity(UUID.randomUUID(), null, name, type, wineryId, regionId)
}