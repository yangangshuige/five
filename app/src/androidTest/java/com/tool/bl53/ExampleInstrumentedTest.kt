package com.tool.bl53

import android.util.Base64
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.test", appContext.packageName)
    }
    @Test
    fun myTest4() {
        val password ="D56E44"
        val data = Base64.encodeToString(password.toByteArray(),
            Base64.NO_WRAP)
        val byte =Base64.decode(data,Base64.DEFAULT)
        println("data================$data")
        println("byte================${byte.size}")
    }
}