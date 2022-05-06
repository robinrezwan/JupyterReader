package com.robinrezwan.jupyterreader.util

import java.time.LocalDateTime

fun LocalDateTime.isSameDate(other: LocalDateTime): Boolean {
    return year == other.year && month == other.month && dayOfMonth == other.dayOfMonth
}

fun LocalDateTime.isSameYear(other: LocalDateTime): Boolean {
    return year == other.year
}
