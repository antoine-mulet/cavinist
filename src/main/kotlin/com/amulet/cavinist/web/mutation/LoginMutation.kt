package com.amulet.cavinist.web.mutation

import com.amulet.cavinist.service.auth.AuthenticationService
import com.amulet.cavinist.web.data.output.auth.LoginOutput
import com.amulet.cavinist.web.data.output.user.UserOutput
import com.expediagroup.graphql.spring.operations.Mutation
import org.springframework.stereotype.Component

@Component
class LoginMutation(private val authenticationService: AuthenticationService) : Mutation {

    suspend fun login(login: String, password: String): LoginOutput? {
        val loginResult = authenticationService.login(login, password)
        return LoginOutput(UserOutput(loginResult.user), loginResult.jwt)
    }
}