package com.krish.foody.util

import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor

/** Human-friendly ingredient amounts: 0.33333334 -> "1/3", 1.5 -> "1 1/2", 2.0 -> "2", 0.2 -> "0.2". */
object AmountFormat {
    private val fractions = listOf(
        0.125 to "1/8", 0.25 to "1/4", 1.0 / 3 to "1/3", 0.375 to "3/8", 0.5 to "1/2",
        0.625 to "5/8", 2.0 / 3 to "2/3", 0.75 to "3/4", 0.875 to "7/8"
    )

    fun format(amount: Double): String {
        if (amount.isNaN() || amount <= 0.0) return ""
        val whole = floor(amount)
        val rest = amount - whole
        if (rest < 0.01) return whole.toLong().toString()
        if (rest > 0.99) return (whole + 1).toLong().toString()
        val match = fractions.firstOrNull { abs(it.first - rest) < 0.02 }?.second
        if (match != null) return if (whole >= 1) "${whole.toLong()} $match" else match
        return String.format(Locale.US, "%.2f", amount).trimEnd('0').trimEnd('.')
    }
}
