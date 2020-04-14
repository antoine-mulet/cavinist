package com.amulet.cavinist.persistence.data

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.*
import java.util.*

@Table("chateaux")
data class ChateauData(
    private val maybeId: UUID?,
    @Column("name") val name: String,
    @Column("region") val region: String) : Persistable<UUID> {

    @Id @Column("id") private var id: UUID = maybeId ?: UUID.randomUUID()

    override fun isNew(): Boolean = maybeId == null
    override fun getId(): UUID = id
}