package com.amulet.cavinist.persistence.data

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.*
import java.util.UUID

@Table("regions")
data class RegionEntity(
    @Id @Column("id") override val id: UUID,
    @Version @Column("version") override var version: Int?,
    @Column("name") val name: String,
    @Column("country") val country: String) : Entity() {

    override fun description(): String = "Region with name '$name', country '$country'"
}
