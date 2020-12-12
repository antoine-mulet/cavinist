package com.amulet.cavinist.web.common

import com.amulet.cavinist.common.WordSpecIT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.regex.Pattern

@AutoConfigureWebTestClient(timeout = "10000")
abstract class WordSpecWebIT : WordSpecIT() {

    companion object Companion {
        val uuidPattern =
            Pattern.compile("[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}")
    }

    @Autowired protected lateinit var testClient: WebTestClient

    fun testQuery(query: String, body: String, jwt: String? = null): WebTestResponse {
        val post = testClient.post()
            .uri("/graphql")
            .accept(APPLICATION_JSON)
            .contentType(MediaType("application", "graphql"))
            .bodyValue(body)
        val postWithAuth = if (jwt == null) post else post.header("Authorization", "Bearer $jwt")
        return WebTestResponse(query, postWithAuth.exchange())
    }
}
