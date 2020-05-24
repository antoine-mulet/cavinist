package com.amulet.cavinist.web.data.output.auth

import com.amulet.cavinist.web.data.output.user.UserOutput
import com.expediagroup.graphql.annotations.GraphQLName

@GraphQLName("Login")
data class LoginOutput(val user: UserOutput, val token: String)