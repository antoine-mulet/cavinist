package com.amulet.cavinist.security

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HmacUtils {

    enum class HmacAlgo(val value: String) {
        SHA_512("HmacSHA512")
    }

    fun sign(data: ByteArray, secret: ByteArray, algo: HmacAlgo): ByteArray {
        val macSecret = SecretKeySpec(secret, algo.value)
        val mac = Mac.getInstance(algo.value)
        mac.init(macSecret)
        return mac.doFinal(data)
    }
}