package com.example

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.model.ShowcaseAnimationMode
import com.example.ui.HeartBloomViewModel
import com.example.ui.components.PreferencesBottomSheet
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ShowcaseScreen
import com.example.ui.theme.HeartBloomTheme
import com.example.ui.theme.ObsidianVelvet

private object HeartBloomRoutes {
    const val HOME = "home"
    const val SHOWCASE = "showcase"
}

class MainActivity : ComponentActivity() {

    private val viewModel: HeartBloomViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ColorDrawable(ObsidianVelvet.toArgb()))
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        setContent {
            HeartBloomApp(viewModel = viewModel)
        }
    }
}

@Composable
private fun HeartBloomApp(viewModel: HeartBloomViewModel) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val showcaseState by viewModel.showcaseState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    var showPreferencesSheet by remember { mutableStateOf(false) }

    HeartBloomTheme(language = preferences.language) {
        NavHost(
            navController = navController,
            startDestination = HeartBloomRoutes.HOME,
            enterTransition = { fadeIn(tween(420)) },
            exitTransition = { fadeOut(tween(320)) },
            popEnterTransition = { EnterTransition.None + fadeIn(tween(360)) },
            popExitTransition = { ExitTransition.None + fadeOut(tween(300)) }
        ) {
            composable(HeartBloomRoutes.HOME) {
                HomeScreen(
                    preferences = preferences,
                    onOpenHeartBloom = {
                        viewModel.startShowcase(
                            mode = ShowcaseAnimationMode.HEART_BLOOM,
                            shuffle = false,
                            favoritesOnly = false
                        )
                        navController.navigate(HeartBloomRoutes.SHOWCASE)
                    },
                    onOpenLoveShowcase = {
                        viewModel.startShowcase(
                            mode = ShowcaseAnimationMode.LOVE_SLIDE,
                            shuffle = false,
                            favoritesOnly = false
                        )
                        navController.navigate(HeartBloomRoutes.SHOWCASE)
                    },
                    onOpenMagicShuffle = {
                        viewModel.startShowcase(
                            mode = ShowcaseAnimationMode.HEART_BLOOM,
                            shuffle = true,
                            favoritesOnly = false
                        )
                        navController.navigate(HeartBloomRoutes.SHOWCASE)
                    },
                    onOpenFavorites = {
                        viewModel.startShowcase(
                            mode = preferences.animationMode,
                            shuffle = false,
                            favoritesOnly = true
                        )
                        navController.navigate(HeartBloomRoutes.SHOWCASE)
                    },
                    onToggleLanguage = viewModel::toggleLanguage,
                    onOpenSettings = { showPreferencesSheet = true }
                )
            }

            composable(HeartBloomRoutes.SHOWCASE) {
                ShowcaseScreen(
                    state = showcaseState,
                    onBack = { navController.popBackStack() },
                    onNext = viewModel::nextPhoto,
                    onPrevious = viewModel::previousPhoto,
                    onTogglePlayPause = viewModel::togglePlayPause,
                    onToggleImmersive = viewModel::toggleImmersive,
                    onSelectAnimationMode = viewModel::setAnimationMode,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onToggleFavoritesFilter = viewModel::toggleFavoritesFilter,
                    onToggleShuffle = viewModel::toggleShuffle,
                    onToggleMusic = viewModel::toggleBackgroundMusic,
                    onOpenSettings = { showPreferencesSheet = true }
                )
            }
        }

        if (showPreferencesSheet) {
            PreferencesBottomSheet(
                preferences = preferences,
                onDismiss = { showPreferencesSheet = false },
                onSelectSpeed = viewModel::setSlideDurationMs,
                onSelectParticleDensity = viewModel::setParticleDensity,
                onSelectBackgroundMusic = viewModel::setBackgroundMusicEnabled,
                onSelectLanguage = viewModel::setLanguage,
                onClearFavorites = viewModel::clearAllFavorites
            )
        }
    }
}
