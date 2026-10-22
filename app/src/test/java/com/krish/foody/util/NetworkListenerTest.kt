package com.krish.foody.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Regression: the network callback was registered on every view creation and never released. */
class NetworkListenerTest {
    @Test
    fun callbackIsRegisteredOnlyOnce() {
        val listener = NetworkListener()
        assertTrue(listener.markRegistered())
        assertFalse(listener.markRegistered())
    }
}
