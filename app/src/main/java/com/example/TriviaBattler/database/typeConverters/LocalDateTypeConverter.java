package com.example.TriviaBattler.database.typeConverters;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class LocalDateTypeConverter {

    /**
     * convert date to long
     * @param date date
     * @return epoch mili
     */
    @TypeConverter
    public long convertDateToLong(LocalDateTime date) {
        ZonedDateTime zdt = ZonedDateTime.of(date, ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
    }

    /**
     * convert long to date
     * @param epochMilli time stamp
     * @return local date
     */
    @TypeConverter
    public LocalDateTime convertLongToDate (Long epochMilli) {
        Instant instant = Instant.ofEpochMilli(epochMilli);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

}
