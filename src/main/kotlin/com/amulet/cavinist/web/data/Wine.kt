package com.amulet.cavinist.web.data

import com.amulet.cavinist.web.context.ChateauServiceContext
import com.amulet.cavinist.web.query.ChateauQuery
import com.expediagroup.graphql.annotations.GraphQLIgnore
import java.util.UUID

class Wine(val id: UUID, val name: String, val year: Int, @GraphQLIgnore val chateauId: UUID) {

    suspend fun chateau(context: ChateauServiceContext): Chateau? {
        return ChateauQuery(context.chateauService).getChateau(chateauId)
    }
}