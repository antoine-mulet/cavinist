package com.amulet.cavinist.persistence.data.user

import com.amulet.cavinist.persistence.data.Entity
import org.springframework.data.annotation.*
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.*
import java.util.UUID

@Table("users")
data class UserEntity(
    @Id @Column("id") override val ID: UUID,
    @Column("login") val login: String,
    @Column("password") val passwordHash: String,
    @Transient private val isNew: Boolean) : Entity() {

    @PersistenceConstructor
    constructor(ID: UUID, login: String, passwordHash: String): this(ID, login, passwordHash, false)

    override fun description(): String = "User with login '$login'"

    override fun isNew(): Boolean = isNew
}