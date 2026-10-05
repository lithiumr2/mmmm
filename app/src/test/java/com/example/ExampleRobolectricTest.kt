package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.logic.SRSEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Tech Survival", appName)
    }

    @Test
    fun `test SuperMemo2 algorithm calculation`() {
        val now = 1000000000L
        val result = SRSEngine.calculateNextReview(
            quality = 5,
            previousInterval = 1,
            previousEaseFactor = 2.5f,
            repetitions = 0,
            currentTimeMillis = now
        )
        assertEquals(1, result.intervalDays)
        assertEquals(1, result.repetitions)
        assertTrue(result.easeFactor >= 2.5f)
        assertTrue(result.nextReviewDateMillis > now)
    }
}
