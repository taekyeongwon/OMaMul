package com.tkw.common.util

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeUtils {

    fun getToday(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return current.format(formatter)
    }

    fun getTodayDate(): String {
        val current = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return current.format(formatter)
    }

    fun getFormattedTime(date: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return getFormattedTime(
            LocalDateTime.parse(date, formatter).hour,
            LocalDateTime.parse(date, formatter).minute
        )
    }

    fun getFullFormatFromDateTime(dateTime: String, hour: Int, minute: Int): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return LocalDateTime.of(
            getDateFromFullFormat(dateTime),
            LocalTime.of(hour, minute)
        ).format(formatter)
    }

    fun getFullFormatFromDate(date: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return LocalDateTime.of(
            getDateFromFormat(date),
            LocalTime.of(0, 0)
        ).format(formatter)
    }

    fun getFormattedTime(hour: Int, minute: Int): String {
        val formatter = DateTimeFormatter.ofPattern("a hh:mm")
        return LocalTime.of(hour, minute).format(formatter)
    }

    fun getFormattedDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter)
    }

    fun getDateFromFullFormat(formatted: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return LocalDate.parse(formatted, formatter)
    }

    fun getDateFromFormat(formatted: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.parse(formatted, formatter)
    }

    fun getTimeFromFullFormat(formatted: String): LocalTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return LocalTime.parse(formatted, formatter)
    }

    fun getTimeFromFormat(formatted: String): LocalTime {
        val formatter = DateTimeFormatter.ofPattern("a hh:mm")
        return LocalTime.parse(formatted, formatter)
    }

    fun getTime(hour: Int, minute: Int, hourFormat: String, minFormat: String): String {
        val formatter = DateTimeFormatter
            .ofPattern("H'$hourFormat' mm'$minFormat'", Locale.getDefault())

        val time = LocalTime.of(hour, minute)
        return time.format(formatter)
    }

    fun getTimeFromLocalTime(formatted: String, hourFormat: String, minFormat: String): LocalTime {
        val formatter = DateTimeFormatter
            .ofPattern("H'$hourFormat' mm'$minFormat'", Locale.getDefault())
        return LocalTime.parse(formatted, formatter)
    }

    fun getWeekDates(date: String): Pair<String, String> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val localDate = getDateFromFormat(date)
        val startOfWeek = localDate.with(DayOfWeek.MONDAY).format(formatter)
        val endOfWeek = localDate.with(DayOfWeek.SUNDAY).format(formatter)
        return startOfWeek to endOfWeek
    }

    fun getIndexOfWeek(date: String): Int {
        val localDate = getDateFromFormat(date)
        return localDate.dayOfWeek.value
    }

    fun getMonthDates(date: String): Pair<String, String> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val localDate = getDateFromFormat(date)
        val startOfMonth = localDate.withDayOfMonth(1).format(formatter)
        val endOfMonth = localDate.withDayOfMonth(localDate.lengthOfMonth()).format(formatter)
        return startOfMonth to endOfMonth
    }

    fun getDateTimeInt(timeInMillis: Long): Int {
        val formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss")
        val dateTime = Instant.ofEpochMilli(timeInMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        val formattedDateTime = dateTime.format(formatter)
        return formattedDateTime.toInt()
    }

    fun addWeek(date: String, add: Long): String {
        val day = if(add > 0) getDateFromFormat(date).plusWeeks(add)
        else getDateFromFormat(date).minusWeeks(add)
        return getFormattedDate(day)
    }

    fun addMonth(date: String, add: Long): String {
        val day = if(add > 0) getDateFromFormat(date).plusMonths(add)
        else getDateFromFormat(date).minusMonths(add)
        return getFormattedDate(day)
    }
}