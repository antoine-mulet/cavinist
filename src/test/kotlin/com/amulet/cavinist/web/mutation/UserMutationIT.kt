package com.amulet.cavinist.web.mutation

import com.amulet.cavinist.web.common.WordSpecWebIT
import com.amulet.cavinist.web.graphql.ErrorCode
import org.hamcrest.text.MatchesPattern

class UserMutationIT : WordSpecWebIT() {

    private val createUserMutation = "createUser"

    init {

        createUserMutation should {

            "create a new user" {

                testQuery(
                    createUserMutation,
                    """mutation { createUser(userInput: {login: "foo", password: "S@fe_P@ssw0rd"}) { id, login }}""")
                    .verifyData(
                        "id" to MatchesPattern(uuidPattern),
                        "login" to "foo"
                               )
            }

            "fail when the login is not compliant" {

                testQuery(
                    createUserMutation,
                    """mutation { createUser(userInput: {login: "x", password: "S@fe_P@ssw0rd"}) { id, login }}""")
                    .verifyError(ErrorCode.LOGIN_POLICY_FAILURE)
            }

            "fail when a user with the same login already exists" {

                testQuery(
                    createUserMutation,
                    """mutation { createUser(userInput: {login: "foo", password: "another_password"}) { id, login }}""")
                    .verifyError(ErrorCode.PASSWORD_POLICY_FAILURE)
            }
        }
    }
}