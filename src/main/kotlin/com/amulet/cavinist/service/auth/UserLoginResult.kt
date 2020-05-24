package com.amulet.cavinist.service.auth

import com.amulet.cavinist.persistence.data.user.UserEntity

data class UserLoginResult(val jwt: String, val user: UserEntity)