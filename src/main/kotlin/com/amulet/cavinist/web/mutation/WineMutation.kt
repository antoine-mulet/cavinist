package com.amulet.cavinist.web.mutation

import com.amulet.cavinist.service.wine.WineService
import com.amulet.cavinist.utils.suspending
import com.amulet.cavinist.web.data.input.wine.WineInput
import com.amulet.cavinist.web.data.output.wine.WineOutput
import com.amulet.cavinist.web.graphql.RequestContext
import com.expediagroup.graphql.spring.operations.Mutation
import org.springframework.stereotype.Component

@Component
class WineMutation(private val wineService: WineService) : Mutation {

    suspend fun createWine(context: RequestContext, wineInput: WineInput): WineOutput? =
        wineService.createWine(wineInput.toNewWine(), context.userId()).suspending()?.let { WineOutput(it) }
}