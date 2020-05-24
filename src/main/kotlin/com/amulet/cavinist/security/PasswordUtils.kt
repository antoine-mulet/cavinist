package com.amulet.cavinist.security

import org.bouncycastle.util.encoders.Hex
import org.passay.*
import org.passay.EnglishSequenceData.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordUtils {

    companion object Companion {
        private const val SALT_LENGTH = 16
        private const val HASH_LENGTH = 32
        private const val PARALLELISM = 2
        private const val MEMORY = 1 shl 15

        private const val PWD_MIN_LENGTH = 8
        private const val PWD_MAX_LENGTH = 50
        private val specialCharacters = object : CharacterData {
            override fun getErrorCode(): String = "SPECIAL_CHAR_POLICY_ERROR"
            override fun getCharacters(): String = "!@#$%^&*_+-=()[]{};:|,<>?"

        }
        private val passwordPolicyDescription = "Passwords must be between $PWD_MIN_LENGTH and $PWD_MAX_LENGTH. " +
            "They must contain at least 1 digit, 1 upper case, 1 lower case " +
            "and 1 of the following special characters '${specialCharacters.characters}'. " +
            "They must not contain the login, whitespaces or more than 2 repeating or sequential characters."
        private val passwordValidator: PasswordValidator = PasswordValidator(
            listOf(
                LengthRule(PWD_MIN_LENGTH, PWD_MAX_LENGTH),
                CharacterRule(EnglishCharacterData.Digit), // at least 1 digit
                CharacterRule(EnglishCharacterData.UpperCase), // at least 1 upper case
                CharacterRule(EnglishCharacterData.LowerCase), // at least 1 lower case
                CharacterRule(specialCharacters), // at least 1 special character
                IllegalSequenceRule(Alphabetical, 3, false), // no more than 2 alphabetical sequential characters
                IllegalSequenceRule(Numerical, 3, false),  // no more than 2 numerical sequential characters
                IllegalSequenceRule(USQwerty, 3, false),  // no more than 2 qwerty keyboard sequential characters
                WhitespaceRule(), // no whitespace
                RepeatCharacterRegexRule(3), // no more than 3 repeating characters
                UsernameRule(true, true)) // password cannot contain login
                                                                            )

        private const val loginPolicyDescription =
            "Logins must be between 3 and 20 characters. Only lower case letters, digits, '_' and '_' are allowed."
        private val loginValidator: PasswordValidator = PasswordValidator(AllowedRegexRule("^[a-z0-9_-]{3,20}\$"))

        sealed class LoginPasswordValidationResult {
            object ValidLoginPassword : LoginPasswordValidationResult()
            class LoginPolicyFailure(val policyDescription: String) : LoginPasswordValidationResult()
            class PasswordPolicyFailure(val policyDescription: String) : LoginPasswordValidationResult()
        }
    }

    @Value("\${security.password.iterations}")
    private val iterations: Int = -1

    @Value("\${security.password.pepper}")
    private lateinit var pepper: String

    private val encoder: PasswordEncoder by lazy {
        Argon2PasswordEncoder(SALT_LENGTH, HASH_LENGTH, PARALLELISM, MEMORY, iterations)
    }

    fun validateLoginPassword(login: String, plainTextPassword: String): LoginPasswordValidationResult =
        if (!loginValidator.validate(PasswordData(login)).isValid)
            LoginPasswordValidationResult.LoginPolicyFailure(loginPolicyDescription)
        else if (!passwordValidator.validate(PasswordData(login, plainTextPassword)).isValid)
            LoginPasswordValidationResult.PasswordPolicyFailure(passwordPolicyDescription)
        else LoginPasswordValidationResult.ValidLoginPassword


    fun hashPassword(plainTextPassword: String): String {
        val passwordHashPepper = sign(plainTextPassword, pepper)
        return encoder.encode(passwordHashPepper)
    }

    fun checkPassword(plainTextCandidate: String, hashedPassword: String): Boolean {
        val candidateHashPepper = sign(plainTextCandidate, pepper)
        return encoder.matches(candidateHashPepper, hashedPassword)
    }

    private fun sign(data: String, secret: String): String =
        Hex.toHexString(HmacUtils.sign(data.toByteArray(), secret.toByteArray(), HmacUtils.HmacAlgo.SHA_512))

}

