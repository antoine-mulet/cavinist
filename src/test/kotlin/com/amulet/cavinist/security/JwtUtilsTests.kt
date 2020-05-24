package com.amulet.cavinist.security

import com.amulet.cavinist.common.WordSpecUnitTests
import com.amulet.cavinist.utils.Clock
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import java.util.*


class JwtUtilsTests : WordSpecUnitTests() {

    @MockkBean
    lateinit var clock: Clock

    @Autowired
    lateinit var obj: JwtUtils

    init {

        "jwt" should {

            val expectedValidity = 3600000

            val now = System.currentTimeMillis()
            val iat = now / 1000 // set as seconds
            val exp = (now + expectedValidity) / 1000 // set as seconds
            every { clock.getCurrentTimeMillis() } returns now
            val id = UUID.randomUUID()
            val jwt = obj.issueJwtForUser(id)

            "have the correct content" {
                val jwtSplit = jwt.split(".")
                val decoder = Base64.getDecoder()
                val header = String(decoder.decode(jwtSplit.first()))
                val claims = String(decoder.decode(jwtSplit[1]))
                val objectMapper = ObjectMapper()
                val headerObj = objectMapper.readTree(header)
                headerObj.get("alg").asText() shouldBe "HS256"
                val claimsObj = objectMapper.readTree(claims)
                println(claims)
                claimsObj.get("iss").asText() shouldBe "cavinist"
                claimsObj.get("sub").asText() shouldBe id.toString()
                claimsObj.get("iat").asLong() shouldBe iat
                claimsObj.get("exp").asLong() shouldBe (iat + expectedValidity / 1000)
            }

            "be decoded correctly" {
                every { clock.getCurrentTimeMillis() } returns (now + expectedValidity - 100000)
                val decoded = obj.decodeJwt(jwt)
                val res = decoded as? JwtUtils.Companion.JwtDecodeResult.ValidJwt
                res?.userId shouldBe id
            }

            "expire after some time" {
                every { clock.getCurrentTimeMillis() } returns (now + expectedValidity + 100000)
                val decoded = obj.decodeJwt(jwt)
                decoded.shouldBeTypeOf<JwtUtils.Companion.JwtDecodeResult.ExpiredJwt>()
            }

            "be protected against tampering" {
                val encoder = Base64.getEncoder().withoutPadding()
                every { clock.getCurrentTimeMillis() } returns now + 1
                val split = jwt.split(".")

                // forging the header
                val forgedHeader = String(encoder.encode("""{"alg":"none"}""".toByteArray()))
                val forgedHeaderJwt = (listOf(forgedHeader) + split.drop(1)).joinToString(".")
                val decoded1 = obj.decodeJwt(forgedHeaderJwt)
                decoded1.shouldBeTypeOf<JwtUtils.Companion.JwtDecodeResult.InvalidJwt>()

                // forging the claims
                val x = """{"iss":"cavinistt","sub":"$id","iat":$iat,"exp":$exp}"""
                val forgedClaims = String(encoder.encode(x.toByteArray()))
                val forgedClaimsJwt = (listOf(split.first(), forgedClaims) + split.drop(2)).joinToString(".")
                val decoded2 = obj.decodeJwt(forgedClaimsJwt)
                decoded2.shouldBeTypeOf<JwtUtils.Companion.JwtDecodeResult.InvalidJwt>()
            }

        }

    }
}