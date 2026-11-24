package com.example.data

import com.example.data.model.MemoryPhoto
import kotlin.random.Random

object MemoryCatalog {

    val allMemories: List<MemoryPhoto> = listOf(
        MemoryPhoto(
            id = "mem_01",
            assetFileName = "FB_IMG_1795515434659_97833258.jpg",
            titleEn = "Riverbank Serenity",
            titleBn = "নদীর তীরে প্রশান্তি",
            moodEn = "Golden Hour",
            moodBn = "গোধূলি বেলা",
            accentHex = 0xFFE8B4B8
        ),
        MemoryPhoto(
            id = "mem_02",
            assetFileName = "FB_IMG_1795515497009_a6d213cf.jpg",
            titleEn = "Crimson Blossom Smile",
            titleBn = "লাল ফুলে মিষ্টি হাসি",
            moodEn = "Garden Grace",
            moodBn = "বাগানের স্নিগ্ধতা",
            accentHex = 0xFFF43F5E
        ),
        MemoryPhoto(
            id = "mem_03",
            assetFileName = "FB_IMG_1795515499301_376ff880.jpg",
            titleEn = "Petals & Sunlight",
            titleBn = "পাপড়ি আর রোদের আলো",
            moodEn = "Radiant Bloom",
            moodBn = "উজ্জ্বল ভালোবাসা",
            accentHex = 0xFFF5D08A
        ),
        MemoryPhoto(
            id = "mem_04",
            assetFileName = "FB_IMG_1795515529745_8a979fc4.jpg",
            titleEn = "Violet Sky Horizon",
            titleBn = "নীল আকাশের দিগন্ত",
            moodEn = "Breeze & Dreams",
            moodBn = "স্বপ্নীল বাতাস",
            accentHex = 0xFFC084FC
        ),
        MemoryPhoto(
            id = "mem_05",
            assetFileName = "FB_IMG_1795515432139_ffc4ca9d.jpg",
            titleEn = "Waves & Whisper",
            titleBn = "ঢেউয়ের মাঝে নীরব কথা",
            moodEn = "Riverside Glow",
            moodBn = "নদীর স্নিগ্ধ আলো",
            accentHex = 0xFFFDA4AF
        ),
        MemoryPhoto(
            id = "mem_06",
            assetFileName = "FB_IMG_1795515532664_58d8ec34.jpg",
            titleEn = "Autumn Reed Meadow",
            titleBn = "কাশফুলের শুভ্র ছোঁয়া",
            moodEn = "Pure Serenity",
            moodBn = "শরতের কোমলতা",
            accentHex = 0xFFA78BFA
        ),
        MemoryPhoto(
            id = "mem_07",
            assetFileName = "FB_IMG_1795515479657_a0a3ce1e.jpg",
            titleEn = "Warm Candlelight Melody",
            titleBn = "উষ্ণ আলোর সুর",
            moodEn = "Golden Velvet",
            moodBn = "সোনালি আভা",
            accentHex = 0xFFFBBF24
        ),
        MemoryPhoto(
            id = "mem_08",
            assetFileName = "FB_IMG_1795515502481_565dfb9f.jpg",
            titleEn = "Swing by the Window",
            titleBn = "জানালার পাশে দোলনা",
            moodEn = "Artistic Reverie",
            moodBn = "শিল্পিত বিকেল",
            accentHex = 0xFFF59E0B
        ),
        MemoryPhoto(
            id = "mem_09",
            assetFileName = "FB_IMG_1795515490527_ca392905.jpg",
            titleEn = "Emerald Lane Walk",
            titleBn = "সবুজ পথের ছায়ায়",
            moodEn = "Soft Footsteps",
            moodBn = "প্রকৃতির স্নেহ",
            accentHex = 0xFFFB7185
        ),
        MemoryPhoto(
            id = "mem_10",
            assetFileName = "FB_IMG_1795515482496_75c4ff6b.jpg",
            titleEn = "Saffron Saree Elegance",
            titleBn = "বাসন্তী শাড়ির মাধুর্য",
            moodEn = "Gallery Muse",
            moodBn = "রঙিন ক্যানভাস",
            accentHex = 0xFFFB923C
        ),
        MemoryPhoto(
            id = "mem_11",
            assetFileName = "FB_IMG_1795515422070_cf65caeb.jpg",
            titleEn = "Meadow Daydream",
            titleBn = "সবুজ ঘাসের কোলে",
            moodEn = "Peaceful Heart",
            moodBn = "প্রশান্ত হৃদয়",
            accentHex = 0xFFE879F9
        ),
        MemoryPhoto(
            id = "mem_12",
            assetFileName = "FB_IMG_1795515403307_0519e9a2.jpg",
            titleEn = "Crimson Arch Peek",
            titleBn = "প্রাচীন খিলানের হাসি",
            moodEn = "Sweet Surprise",
            moodBn = "মিষ্টি লুকোচুরি",
            accentHex = 0xFFE11D48
        ),
        MemoryPhoto(
            id = "mem_13",
            assetFileName = "FB_IMG_1795515394234_d8cc3ce0.jpg",
            titleEn = "Plumeria by Heritage",
            titleBn = "কাঠগোলাপের স্নিগ্ধতা",
            moodEn = "Rose Radiance",
            moodBn = "গোলাপি উজ্জ্বলতা",
            accentHex = 0xFFEC4899
        ),
        MemoryPhoto(
            id = "mem_14",
            assetFileName = "FB_IMG_1795515554495_b8f82348.jpg",
            titleEn = "Bougainvillea Canopy",
            titleBn = "বাগানবিলাসের ছায়াতলে",
            moodEn = "Sky & Bloom",
            moodBn = "নীল আর গোলাপি",
            accentHex = 0xFF38BDF8
        ),
        MemoryPhoto(
            id = "mem_15",
            assetFileName = "FB_IMG_1795515444460_daf72b19.jpg",
            titleEn = "Ruby Silk Afternoon",
            titleBn = "রক্তিম শাড়িতে বিকেল",
            moodEn = "Royal Grace",
            moodBn = "রাজকীয় সৌন্দর্য",
            accentHex = 0xFFF43F5E
        ),
        MemoryPhoto(
            id = "mem_16",
            assetFileName = "FB_IMG_1795515442241_da103bb3.jpg",
            titleEn = "Sunset Garden Smile",
            titleBn = "গোধূলির মিষ্টি হাসি",
            moodEn = "Warm Joy",
            moodBn = "উষ্ণ আনন্দ",
            accentHex = 0xFFFB7185
        ),
        MemoryPhoto(
            id = "mem_17",
            assetFileName = "FB_IMG_1795515546636_8256c90f.jpg",
            titleEn = "Terracotta Reverie",
            titleBn = "ইটের দেয়ালে কাব্য",
            moodEn = "Vintage Rose",
            moodBn = "চিরন্তন মায়া",
            accentHex = 0xFFFDA4AF
        ),
        MemoryPhoto(
            id = "mem_18",
            assetFileName = "FB_IMG_1795515557162_e50eeb66.jpg",
            titleEn = "Azure & Magenta",
            titleBn = "ফুলের মাঝে হাসির ঝলক",
            moodEn = "Spring Whisper",
            moodBn = "বসন্তের ছোঁয়া",
            accentHex = 0xFF60A5FA
        ),
        MemoryPhoto(
            id = "mem_19",
            assetFileName = "FB_IMG_1795515417546_a33201b6.jpg",
            titleEn = "Emerald Lawn Stillness",
            titleBn = "নীরব দুপুরের মুগ্ধতা",
            moodEn = "Soft Glance",
            moodBn = "স্নিগ্ধ চাহনি",
            accentHex = 0xFFD8B4FE
        ),
        MemoryPhoto(
            id = "mem_20",
            assetFileName = "FB_IMG_1795515407292_2ab94e26.jpg",
            titleEn = "Blossom in Hand",
            titleBn = "হাতে সাদা ফুলের গল্প",
            moodEn = "Delicate Moment",
            moodBn = "কোমল মুহূর্ত",
            accentHex = 0xFFF472B6
        ),
        MemoryPhoto(
            id = "mem_21",
            assetFileName = "FB_IMG_1795515544120_aa5f660b.jpg",
            titleEn = "Ancient Brick Muse",
            titleBn = "পুরনো দেয়ালের মায়াবী আলো",
            moodEn = "Timeless Portrait",
            moodBn = "অনন্ত স্মৃতি",
            accentHex = 0xFFFCA5A5
        ),
        MemoryPhoto(
            id = "mem_22",
            assetFileName = "FB_IMG_1795515447821_55923dde.jpg",
            titleEn = "Lantern Walk Path",
            titleBn = "আলোর তোরণ পেরিয়ে",
            moodEn = "Evening Magic",
            moodBn = "সন্ধ্যার জাদু",
            accentHex = 0xFFF5D08A
        ),
        MemoryPhoto(
            id = "mem_23",
            assetFileName = "FB_IMG_1795515453144_87e7932a.jpg",
            titleEn = "Courtyard Crimson",
            titleBn = "সবুজ আঙিনায় লাল আভা",
            moodEn = "Velvet Charm",
            moodBn = "মখমলি আভা",
            accentHex = 0xFFE11D48
        ),
        MemoryPhoto(
            id = "mem_24",
            assetFileName = "FB_IMG_1795515400581_a94c85b8.jpg",
            titleEn = "Sunbeam Sanctuary",
            titleBn = "বৃক্ষছায়ায় আলোর পরশ",
            moodEn = "Golden Sky",
            moodBn = "সোনালি আকাশ",
            accentHex = 0xFFFBBF24
        )
    )

    val loveWhispersEn: List<String> = listOf(
        "Every heartbeat holds a cherished frame",
        "Moments that bloom softly into forever",
        "Crafted in rose light and golden memory",
        "Tap to awaken the gallery of memories"
    )

    val loveWhispersBn: List<String> = listOf(
        "প্রতিটি হৃদস্পন্দনে জমে থাকে প্রিয় স্মৃতি",
        "ভালোবাসার রঙেই ফুটে ওঠে প্রতিটি মুহূর্ত",
        "সোনালি আলোয় গাঁথা এক একটি গল্প",
        "ছুঁয়ে দিলেই পাপড়ির মতো খুলে যাবে ছবিগুলো"
    )

    fun nextIndex(currentIndex: Int, totalCount: Int): Int {
        if (totalCount <= 0) return 0
        return (currentIndex + 1) % totalCount
    }

    fun previousIndex(currentIndex: Int, totalCount: Int): Int {
        if (totalCount <= 0) return 0
        return (currentIndex - 1 + totalCount) % totalCount
    }

    fun orderedMemories(
        favoritesOnly: Boolean,
        favoriteIds: Set<String>,
        shuffleEnabled: Boolean,
        shuffleSeed: Long
    ): List<MemoryPhoto> {
        val base = if (favoritesOnly) {
            allMemories.filter { it.id in favoriteIds }.ifEmpty { allMemories }
        } else {
            allMemories
        }
        return if (shuffleEnabled) {
            base.shuffled(Random(shuffleSeed))
        } else {
            base
        }
    }
}
