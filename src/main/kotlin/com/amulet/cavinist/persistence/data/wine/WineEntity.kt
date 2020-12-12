package com.amulet.cavinist.persistence.data.wine

import com.amulet.cavinist.persistence.data.VersionedEntity
import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.*
import java.util.UUID

@Table("wines")
data class WineEntity(
    @Id @Column("id") override val ID: UUID,
    @Version @Column("version") override val version: Int?,
    @Column("name") val name: String,
    @Column("type") val type: WineType,
    @Column("winery_id") val wineryId: UUID,
    @Column("region_id") val regionId: UUID,
    @Column("user_id") val userId: UUID) : VersionedEntity() {

    override fun description(): String =
        "Wine with name '$name', type '$type', winery id '$wineryId' and region id '$regionId'"
}