package com.amulet.cavinist.security

import com.amulet.cavinist.utils.Clock
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtils(private val clock: Clock) {

    companion object Companion {
        sealed class JwtDecodeResult {
            data class ValidJwt(val userId: UUID) : JwtDecodeResult()
            object ExpiredJwt : JwtDecodeResult()
            object InvalidJwt : JwtDecodeResult()
        }
    }

    @Value("\${security.jwt.secret}")
    private lateinit var jwtSecret: String

    @Value("\${security.jwt.issuer}")
    private lateinit var jwtIssuer: String

    @Value("\${security.jwt.validity}")
    private val jwtValidity: Int = 0


    private val jwtSigningKey by lazy {
        Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    }

    fun issueJwtForUser(userId: UUID): String {
        val currentTime = clock.getCurrentTimeMillis()
        val expiration = Date(System.currentTimeMillis() + jwtValidity * 1000)
        return Jwts.builder()
            .setIssuer(jwtIssuer)
            .setSubject(userId.toString())
            .setIssuedAt(Date(currentTime))
            .setExpiration(expiration)
            .signWith(jwtSigningKey)
            .compact()!!
    }

    fun decodeJwt(jwt: String): JwtDecodeResult =
        try {
            val verificationClock = io.jsonwebtoken.Clock { Date(clock.getCurrentTimeMillis()) }
            val token = Jwts.parserBuilder()
                .requireIssuer(jwtIssuer)
                .setClock(verificationClock)
                .setSigningKey(jwtSigningKey)
                .build()
                .parseClaimsJws(jwt)!!
            JwtDecodeResult.ValidJwt(UUID.fromString(token.body.subject!!))
        } catch (e: Throwable) {
            when (e) {
                is ExpiredJwtException -> JwtDecodeResult.ExpiredJwt
                else                   -> JwtDecodeResult.InvalidJwt
            }
        }
}