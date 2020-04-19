package com.amulet.cavinist.web.common

import com.amulet.cavinist.common.WordSpecIT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient

@AutoConfigureWebTestClient
abstract class WordSpecWebIT : WordSpecIT() {

    @Autowired protected lateinit var testClient: WebTestClient

    fun testQuery(query: String, body: String): WebTestResponse {
        val post = testClient.post()
            .uri("/graphql")
            .accept(APPLICATION_JSON)
            .contentType(MediaType("application", "graphql"))
            .bodyValue(body)
        return WebTestResponse(query, post.exchange())
    }
}
