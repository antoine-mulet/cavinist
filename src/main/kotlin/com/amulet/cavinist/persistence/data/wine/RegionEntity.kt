package com.amulet.cavinist.persistence.data.wine

import com.amulet.cavinist.persistence.data.VersionedEntity
import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.*
import java.util.UUID

@Immutable
@Table("regions")
data class RegionEntity(
    @Id @Column("id") override val ID: UUID,
    @Version @Column("version") override val version: Int?,
    @Column("name") val name: String,
    @Column("country") val country: String) : VersionedEntity() {

    override fun description(): String = "Region with name '$name', country '$country'"
}
