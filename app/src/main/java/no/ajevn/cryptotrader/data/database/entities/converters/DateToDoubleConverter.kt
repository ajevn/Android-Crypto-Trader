package no.ajevn.cryptotrader.data.database.entities.converters

import androidx.room.TypeConverter
import java.util.*

class DateToDoubleConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}