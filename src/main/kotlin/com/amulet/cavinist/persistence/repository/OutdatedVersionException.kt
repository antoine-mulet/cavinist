package com.amulet.cavinist.persistence.repository

import org.springframework.dao.NonTransientDataAccessException

class OutdatedVersionException(msg: String) : NonTransientDataAccessException(msg)