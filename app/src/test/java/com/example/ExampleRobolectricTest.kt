package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.MemoryCatalog
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
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Srabone", appName)
    }

    @Test
    fun `all 24 catalog memory assets exist in apk assets`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val bundledFiles = context.assets.list("memories")?.toSet().orEmpty()
        assertEquals(24, MemoryCatalog.allMemories.size)
        MemoryCatalog.allMemories.forEach { photo ->
            assertTrue(
                "Missing asset file: ${photo.assetFileName}",
                photo.assetFileName in bundledFiles
            )
        }
    }
}
