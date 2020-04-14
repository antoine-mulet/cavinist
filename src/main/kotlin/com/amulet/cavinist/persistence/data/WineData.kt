package com.amulet.cavinist.persistence.data

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.*
import java.util.*

@Table("wines")
data class WineData(
    private val maybeId: UUID?,
    @Column("chateau_id") val chateauId: UUID,
    @Column("name") val name: String,
    @Column("year") val year: Int) : Persistable<UUID> {

    @Id @Column("id") private var id: UUID = maybeId ?: UUID.randomUUID()

    override fun isNew(): Boolean = maybeId == null
    override fun getId(): UUID = id
}