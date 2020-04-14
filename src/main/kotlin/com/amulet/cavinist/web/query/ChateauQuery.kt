package com.amulet.cavinist.web.query

import com.amulet.cavinist.service.ChateauService
import com.amulet.cavinist.web.data.Chateau
import com.expediagroup.graphql.spring.operations.Query
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ChateauQuery(val chateauService: ChateauService) : Query {

    suspend fun getChateau(id: UUID): Chateau? {
        val maybeChateau = chateauService.getChateau(id)
        return maybeChateau?.let { Chateau(it.id, it.name, it.region) }
    }

    suspend fun listChateaux(): List<Chateau> {
        return chateauService.listChateaux().map { Chateau(it.id, it.name, it.region) }
    }
}