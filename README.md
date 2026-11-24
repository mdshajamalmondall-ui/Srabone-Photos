# Srabone

A luxury animated photo memory showcase Android application built with Kotlin, Jetpack Compose, Material 3, and Coil.

## Features

- **Photo-Free Animated Home Screen**: Deep velvet rose-gold atmospheric backdrop with floating sparkles, interactive heartbeat lotus centerpiece, **Heart Bloom**, and **Love** entry modes.
- **4 Live Showcase Animations**: Switch seamlessly between **Heart Bloom**, **Love Slide**, **Velvet Fade**, and **Memory Card** transitions with slow Ken Burns motion.
- **Synchronized Background Music**: Automatically starts playing *Dilruba Dilnasheen* as background audio when photos begin showcasing, pauses cleanly on lifecycle pause, and stops when returning to Home.
- **Interactive Gestures**: Swipe left/right to browse, single-tap for immersive fullscreen, and double-tap anywhere for a heart-burst reaction that saves favorites.
- **Bilingual Support**: Instant switching between English and Bangla (`EN / বাংলা`).

## Tech Stack

- Kotlin & Jetpack Compose (Material 3, Navigation Compose)
- Custom `Canvas` particle & heart-bloom animations
- Android `MediaPlayer` with lifecycle-aware fade-in audio control
- Coil for high-resolution bundled asset preloading
- Jetpack DataStore for preferences & favorites persistence
