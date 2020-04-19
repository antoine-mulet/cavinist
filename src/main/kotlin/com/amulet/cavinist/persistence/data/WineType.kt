package com.amulet.cavinist.persistence.data

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.*

enum class WineType {
    RED, WHITE, SPARKLING, ROSE, OTHER;
}

@WritingConverter
object WineTypeToIntegerConverter : Converter<WineType, Int> {

    override fun convert(myEnum: WineType): Int {
        return myEnum.ordinal
    }
}

@ReadingConverter
object IntegerToWineTypeConverter : Converter<Int, WineType> {

    override fun convert(source: Int): WineType? {
        return when (source) {
            0    -> WineType.RED
            1    -> WineType.WHITE
            2    -> WineType.SPARKLING
            3    -> WineType.ROSE
            else -> WineType.OTHER
        }
    }
}