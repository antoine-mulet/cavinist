package com.amulet.cavinist.web.mutation

import com.amulet.cavinist.service.*
import com.amulet.cavinist.web.data.*
import com.expediagroup.graphql.spring.operations.Mutation
import org.springframework.stereotype.Component

@Component
class WineMutation(val wineService: WineService, val chateauService: ChateauService) : Mutation {

    suspend fun createWine(name: String, year: Int, chateauInput: ChateauInput): Wine? {
        val chateau = chateauService.createChateau(chateauInput.name, chateauInput.region)
        val wine = chateau?.id?.let { wineService.createWine(name, year, it) }
        return wine?.let { Wine(it.id, it.name, it.year, it.chateauId) }
    }
}