package com.krish.foody.util

import org.junit.Assert.assertEquals
import org.junit.Test

class AmountFormatTest {
    @Test
    fun fractionsAreReadable() {
        assertEquals("1/3", AmountFormat.format(0.33333334))
        assertEquals("1 1/2", AmountFormat.format(1.5))
        assertEquals("1/4", AmountFormat.format(0.25))
        assertEquals("2", AmountFormat.format(2.0))
        assertEquals("2", AmountFormat.format(1.996))
    }

    @Test
    fun otherDecimalsAreShort() {
        assertEquals("0.2", AmountFormat.format(0.2))
        assertEquals("12.4", AmountFormat.format(12.4))
        assertEquals("", AmountFormat.format(0.0))
    }
}
