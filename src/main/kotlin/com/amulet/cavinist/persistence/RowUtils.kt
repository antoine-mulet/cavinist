package com.amulet.cavinist.persistence

import com.amulet.cavinist.persistence.data.wine.*
import io.r2dbc.spi.Row
import java.util.UUID

fun Row.getInt(name: String): Int = this.get(name, Integer::class.java)!! as Int
fun Row.getUUID(name: String): UUID = this.get(name, UUID::class.java)!!
fun Row.getString(name: String): String = this.get(name, String::class.java)!!
fun Row.getWineType(name: String): WineType = IntegerToWineTypeConverter.convert(this.getInt(name))!!