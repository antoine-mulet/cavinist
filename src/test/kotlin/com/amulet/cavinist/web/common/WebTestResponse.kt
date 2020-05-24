package com.amulet.cavinist.web.common

import com.amulet.cavinist.web.graphql.ErrorCode
import org.hamcrest.Matcher
import org.springframework.test.web.reactive.server.WebTestClient

const val DATA_JSON_PATH = "$.data"
const val ERRORS_JSON_PATH = "$.errors"
const val EXTENSIONS_JSON_PATH = "$.extensions"

data class WebTestResponse(val query: String, val response: WebTestClient.ResponseSpec) {

    fun verifyEmpty(): WebTestClient.BodyContentSpec {
        return response.expectStatus().isOk.expectBody()
            .jsonPath(ERRORS_JSON_PATH)
            .doesNotExist()
            .jsonPath("$DATA_JSON_PATH.$query")
            .isEmpty
            .jsonPath(EXTENSIONS_JSON_PATH)
            .doesNotExist()
    }

    fun verifyError(expectedCode: ErrorCode, expectedErrorMessage: String? = null): WebTestClient.BodyContentSpec {
        val r = response.expectStatus().isOk.expectBody()
            .jsonPath("$ERRORS_JSON_PATH[0].extensions.code")
            .isEqualTo(expectedCode.name)
        return if (expectedErrorMessage != null)
            r.jsonPath("$ERRORS_JSON_PATH[0].message").isEqualTo(expectedErrorMessage)
        else r
    }

    fun verifyData(vararg assertions: Pair<String, Any>) {
        val response = verifyOnlyDataExists()
        assertions.forEach { (path, expected) ->
            when (expected) {
                is String     -> response.jsonPath("$DATA_JSON_PATH.$query.$path").isEqualTo(expected)
                is Matcher<*> -> response.jsonPath("$DATA_JSON_PATH.$query.$path").value(expected)
            }
        }
    }

    fun verifyArraySize(expectedSize: Int) {
        verifyOnlyDataExists()
            .jsonPath("$DATA_JSON_PATH.$query.length()")
            .isEqualTo(expectedSize)
    }

    private fun verifyOnlyDataExists(): WebTestClient.BodyContentSpec {
        return response.expectStatus().isOk.expectBody()
            .jsonPath(ERRORS_JSON_PATH)
            .doesNotExist()
            .jsonPath("$DATA_JSON_PATH.$query")
            .exists()
            .jsonPath(EXTENSIONS_JSON_PATH)
            .doesNotExist()
    }
}