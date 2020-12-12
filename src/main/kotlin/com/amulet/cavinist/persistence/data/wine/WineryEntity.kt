package com.amulet.cavinist.persistence.data.wine

import com.amulet.cavinist.persistence.data.VersionedEntity
import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.*
import java.util.UUID

@Table("wineries")
data class WineryEntity(
    @Id @Column("id") override val ID: UUID,
    @Version @Column("version") override val version: Int?,
    @Column("name") val name: String,
    @Column("region_id") val regionId: UUID,
    @Column("user_id") val userId: UUID) : VersionedEntity() {

    override fun description(): String = "Winery with name '$name', region id '$regionId'"
}