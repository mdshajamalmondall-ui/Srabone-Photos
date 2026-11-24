package com.example

import com.example.data.MemoryCatalog
import com.example.data.model.AppLanguage
import com.example.ui.theme.stringsForLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class HeartBloomTest {

    @Test
    fun `catalog contains all 24 unique memory entries`() {
        assertEquals(24, MemoryCatalog.allMemories.size)
        assertEquals(24, MemoryCatalog.allMemories.map { it.id }.toSet().size)
        assertEquals(24, MemoryCatalog.allMemories.map { it.assetFileName }.toSet().size)
    }

    @Test
    fun `all 24 memory asset files exist on disk`() {
        val assetsDir = listOf(
            File("src/main/assets/memories"),
            File("app/src/main/assets/memories")
        ).firstOrNull { it.isDirectory }
        assertTrue("Expected bundled memories directory", assetsDir != null)
        val fileNames = assetsDir!!.list()?.toSet().orEmpty()
        MemoryCatalog.allMemories.forEach { photo ->
            assertTrue(
                "Missing bundled memory asset: ${photo.assetFileName}",
                photo.assetFileName in fileNames
            )
        }
    }

    @Test
    fun `index cycling wraps smoothly in both directions`() {
        assertEquals(1, MemoryCatalog.nextIndex(0, 24))
        assertEquals(0, MemoryCatalog.nextIndex(23, 24))
        assertEquals(23, MemoryCatalog.previousIndex(0, 24))
        assertEquals(22, MemoryCatalog.previousIndex(23, 24))
    }

    @Test
    fun `favorites only ordering filters down when favorites exist`() {
        val favs = setOf("mem_02", "mem_15")
        val filtered = MemoryCatalog.orderedMemories(
            favoritesOnly = true,
            favoriteIds = favs,
            shuffleEnabled = false,
            shuffleSeed = 1L
        )
        assertEquals(listOf("mem_02", "mem_15"), filtered.map { it.id })
    }

    @Test
    fun `background audio file exists in raw resources`() {
        val audioFile = listOf(
            File("src/main/res/raw/dilruba_dilnasheen.m4a"),
            File("app/src/main/res/raw/dilruba_dilnasheen.m4a")
        ).firstOrNull { it.isFile }
        assertTrue("Expected bundled background song in res/raw", audioFile != null && audioFile.length() > 100_000L)
    }

    @Test
    fun `localized strings provide Srabone title and heart bloom love labels`() {
        val en = stringsForLanguage(AppLanguage.EN)
        val bn = stringsForLanguage(AppLanguage.BN)
        assertEquals("Srabone", en.appTitle)
        assertEquals("Srabone", bn.appTitle)
        assertEquals("Heart Bloom", en.heartBloomTitle)
        assertEquals("Love", en.loveTitle)
        assertTrue(bn.heartBloomTitle.isNotBlank())
        assertTrue(bn.loveTitle.isNotBlank())
    }
}
