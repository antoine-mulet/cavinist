package com.amulet.cavinist.persistence.data.wine

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.*

enum class WineType {
    RED, WHITE, ROSE, SPARKLING, OTHER;
}

@WritingConverter
object WineTypeToIntConverter : Converter<WineType, Int> {

    override fun convert(myEnum: WineType): Int = myEnum.ordinal
}

@ReadingConverter
object IntToWineTypeConverter : Converter<Int, WineType> {

    override fun convert(source: Int): WineType? = WineType.values()[source]
}