package com.example.pomodoro

private const val TIME_FORMAT = "%02d:%02d:%02d"

fun Long.displayTime(): String {
    if (this <= 0L) {
        return TIME_FORMAT.format(0, 0, 0)
    }
    val h = this / 1000 / 3600
    val m = this / 1000 % 3600 / 60
    val s = this / 1000 % 60
    return TIME_FORMAT.format(h, m, s)
}

fun Long.minutesToMillis(): Long {
    return this * 60 * 1000
}