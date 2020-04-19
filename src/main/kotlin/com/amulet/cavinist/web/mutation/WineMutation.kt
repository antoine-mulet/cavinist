package com.amulet.cavinist.web.mutation

import com.amulet.cavinist.web.context.ServiceContext
import com.amulet.cavinist.web.data.input.WineInput
import com.amulet.cavinist.web.data.output.WineOutput
import com.expediagroup.graphql.spring.operations.Mutation
import org.springframework.stereotype.Component

@Component
object WineMutation : Mutation {

    suspend fun createWine(context: ServiceContext, wineInput: WineInput): WineOutput? {
        val wine = context.wineService.createWine(wineInput)
        return wine?.let { WineOutput(it) }
    }
}