package com.amulet.cavinist.web.data.output.user

import com.amulet.cavinist.persistence.data.user.UserEntity
import com.expediagroup.graphql.annotations.GraphQLName
import java.util.UUID

@GraphQLName("User")
data class UserOutput(val id: UUID, val login: String) {

    constructor(userEntity: UserEntity): this(userEntity.ID, userEntity.login)
}