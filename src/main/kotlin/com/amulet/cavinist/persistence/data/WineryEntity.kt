package com.amulet.cavinist.persistence.data

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.*
import java.util.UUID

@Table("wineries")
data class WineryEntity(
    @Id @Column("id") override val id: UUID,
    @Version @Column("version") override var version: Int?,
    @Column("name") val name: String,
    @Column("region_id") val regionId: UUID) : Entity() {

    override fun description(): String = "Winery with name '$name' and region id '$regionId'"
}