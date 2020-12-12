package com.amulet.cavinist.persistence.data.wine

import org.springframework.stereotype.Component
import java.util.UUID

interface WineEntityFactory {

    fun newRegion(name: String, country: String, userId: UUID): RegionEntity

    fun newWinery(name: String, regionId: UUID, userId: UUID): WineryEntity

    fun newWine(name: String, type: WineType, wineryId: UUID, regionId: UUID, userId: UUID): WineEntity
}

@Component
object WineEntityFactoryImpl : WineEntityFactory {

    override fun newRegion(name: String, country: String, userId: UUID): RegionEntity =
        RegionEntity(UUID.randomUUID(), null, name, country, userId)

    override fun newWinery(name: String, regionId: UUID, userId: UUID): WineryEntity =
        WineryEntity(UUID.randomUUID(), null, name, regionId, userId)

    override fun newWine(name: String, type: WineType, wineryId: UUID, regionId: UUID, userId: UUID): WineEntity =
        WineEntity(UUID.randomUUID(), null, name, type, wineryId, regionId, userId)
}