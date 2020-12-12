package com.amulet.cavinist.web.mutation

import com.amulet.cavinist.web.common.WordSpecWebIT
import com.amulet.cavinist.web.graphql.ErrorCode
import org.hamcrest.core.StringStartsWith

class LoginMutationIT : WordSpecWebIT() {

    val loginMutation = "login"

    init {

        loginMutation should {

            "issue a JWT to an existing user who is providing the correct login and password" {

                testQuery(
                    loginMutation,
                    """mutation { login(login: "${dataSet.userOne.login}", password: "Th!sIs@Saf3PwdToHash") { user { id, login}, token }}""")
                    .verifyData(
                        "user.id" to dataSet.userOne.id.toString(),
                        "user.login" to dataSet.userOne.login,
                        "token" to StringStartsWith("ey"))

            }

            "fail when the login doesn't match" {

                testQuery(
                    loginMutation,
                    """mutation { login(login: "fake_login", password: "Th!sIs@Saf3PwdToHash") { user { id, login}, token }}""")
                    .verifyError(ErrorCode.AUTH_FAILURE, "Login or password is incorrect.")

            }

            "fail when the password doesn't match" {

                testQuery(
                    loginMutation,
                    """mutation { login(login: "${dataSet.userOne.login}", password: "Th!sIs@Saf3PwdToHashh") { user { id, login}, token }}""")
                    .verifyError(ErrorCode.AUTH_FAILURE, "Login or password is incorrect.")

            }
        }
    }
}