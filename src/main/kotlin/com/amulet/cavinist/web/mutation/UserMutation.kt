package com.amulet.cavinist.web.mutation

import com.amulet.cavinist.service.user.UserService
import com.amulet.cavinist.web.data.input.user.UserInput
import com.amulet.cavinist.web.data.output.user.UserOutput
import com.expediagroup.graphql.spring.operations.Mutation
import org.springframework.stereotype.Component

@Component
class UserMutation(private val userService: UserService) : Mutation {

    suspend fun createUser(userInput: UserInput): UserOutput? =
        userService.createUser(userInput.login, userInput.password)?.let { UserOutput(it) }
}