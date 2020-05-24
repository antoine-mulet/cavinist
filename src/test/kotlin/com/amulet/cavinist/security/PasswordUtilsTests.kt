package com.amulet.cavinist.security

import com.amulet.cavinist.common.WordSpecUnitTests
import com.amulet.cavinist.security.PasswordUtils.Companion.LoginPasswordValidationResult.*
import io.kotest.matchers.*
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.types.shouldBeTypeOf
import org.springframework.beans.factory.annotation.Autowired

class PasswordUtilsTests : WordSpecUnitTests() {

    @Autowired
    lateinit var obj: PasswordUtils

    init {

        val password = "Th!sIs@Saf3PwdToHash"

        "Password hashes" should {

            val hash = obj.hashPassword(password)

            "be created using Argon2id" {
                hash shouldStartWith "\$argon2id"
            }

            "always be different thanks to the salting" {
                hash shouldNotBe obj.hashPassword(password)
            }

            "only match when the password is identical" {
                obj.checkPassword(password, hash) shouldBe true
                obj.checkPassword("Th!sIs@noth3rPwdToHash", hash) shouldBe false
            }
        }

        "Login and password validation" should {

            "be successful when both the login and password are compliant" {
                obj.validateLoginPassword("valid_login", password).shouldBeTypeOf<ValidLoginPassword>()
            }

            "fail when the login is not compliant" {
                obj.validateLoginPassword("l0g\$in", password).shouldBeTypeOf<LoginPolicyFailure>()
                obj.validateLoginPassword("Login", password).shouldBeTypeOf<LoginPolicyFailure>()
                obj.validateLoginPassword("yo", password).shouldBeTypeOf<LoginPolicyFailure>()
            }

            "fail when the password is not compliant" {
                obj.validateLoginPassword("login", "weak").shouldBeTypeOf<PasswordPolicyFailure>()
                obj.validateLoginPassword("login", "weak_password").shouldBeTypeOf<PasswordPolicyFailure>()
                obj.validateLoginPassword("login", "Weak_password").shouldBeTypeOf<PasswordPolicyFailure>()
                obj.validateLoginPassword("login", "Weeeeak_42").shouldBeTypeOf<PasswordPolicyFailure>()
                obj.validateLoginPassword("login", "Weak _ 4242").shouldBeTypeOf<PasswordPolicyFailure>()
                obj.validateLoginPassword("login", "PwdAndLogin42").shouldBeTypeOf<PasswordPolicyFailure>()
                obj.validateLoginPassword("login", "PwdAndLogin123").shouldBeTypeOf<PasswordPolicyFailure>()
                obj.validateLoginPassword("login", "Pwd_abc42").shouldBeTypeOf<PasswordPolicyFailure>()
                obj.validateLoginPassword("login", "Pwd_qwe42").shouldBeTypeOf<PasswordPolicyFailure>()
            }
        }
    }
}