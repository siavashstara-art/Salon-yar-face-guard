package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.BanootViewModel
import com.example.ui.screens.AiStylistScreen
import com.example.ui.screens.BridalCatalogScreen
import com.example.ui.screens.SavedLooksScreen
import com.example.ui.screens.VirtualTryOnScreen
import com.example.ui.theme.BanootTheme
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DeepBurgundy
import com.example.ui.theme.RoseGoldPrimary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BanootTheme(darkTheme = true) {
                val viewModel: BanootViewModel = viewModel()
                val currentScreen by viewModel.currentScreen.collectAsState()
                val isFarsi by viewModel.isFarsi.collectAsState()
                val feedbackMessage by viewModel.feedbackMessage.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                // Show feedback message if present
                LaunchedEffect(feedbackMessage) {
                    feedbackMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearMessage()
                    }
                }

                // Handle system back navigation smoothly
                BackHandler(enabled = currentScreen != AppScreen.TRY_ON) {
                    viewModel.navigateTo(AppScreen.TRY_ON)
                }

                // Bilingual layout direction: RTL for Persian, LTR for English
                val layoutDirection = if (isFarsi) LayoutDirection.Rtl else LayoutDirection.Ltr
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding(),
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        topBar = {
                            BanootTopBar(
                                isFarsi = isFarsi,
                                onToggleLanguage = { viewModel.toggleLanguage() }
                            )
                        },
                        bottomBar = {
                            BanootBottomNav(
                                currentScreen = currentScreen,
                                isFarsi = isFarsi,
                                onSelectScreen = { viewModel.navigateTo(it) }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentScreen) {
                                AppScreen.TRY_ON -> VirtualTryOnScreen(viewModel = viewModel)
                                AppScreen.CATALOG -> BridalCatalogScreen(viewModel = viewModel)
                                AppScreen.AI_STYLIST -> AiStylistScreen(viewModel = viewModel)
                                AppScreen.LOOKBOOK -> SavedLooksScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BanootTopBar(
    isFarsi: Boolean,
    onToggleLanguage: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = DeepBurgundy,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Diamond,
                            contentDescription = "Banoot Logo",
                            tint = ChampagneGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isFarsi) "مزون بانوت | پرو لباس عروس" else "Banoot Atelier | Bridal Try-On",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = ChampagneGold
                    )
                    Text(
                        text = "VIRTUAL FITTING ROOM & HAUTE COUTURE",
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        color = RoseGoldPrimary
                    )
                }
            }
        },
        actions = {
            // Language Toggle Button
            OutlinedButton(
                onClick = onToggleLanguage,
                shape = CircleShape,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.5f)),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .testTag("language_toggle_button")
            ) {
                Text(
                    text = if (isFarsi) "EN" else "فا",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = ChampagneGold
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF140D12),
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun BanootBottomNav(
    currentScreen: AppScreen,
    isFarsi: Boolean,
    onSelectScreen: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF170E15),
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("bottom_nav_bar")
    ) {
        NavigationBarItem(
            selected = currentScreen == AppScreen.TRY_ON,
            onClick = { onSelectScreen(AppScreen.TRY_ON) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.TRY_ON) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                    contentDescription = "Virtual Try-On"
                )
            },
            label = {
                Text(
                    text = if (isFarsi) "پرو لباس عروس" else "Try-On",
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == AppScreen.TRY_ON) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF140D12),
                selectedTextColor = ChampagneGold,
                indicatorColor = RoseGoldPrimary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.CATALOG,
            onClick = { onSelectScreen(AppScreen.CATALOG) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.CATALOG) Icons.Filled.Checkroom else Icons.Outlined.Checkroom,
                    contentDescription = "Catalog"
                )
            },
            label = {
                Text(
                    text = if (isFarsi) "کاتالوگ مزون" else "Catalog",
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == AppScreen.CATALOG) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF140D12),
                selectedTextColor = ChampagneGold,
                indicatorColor = RoseGoldPrimary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.AI_STYLIST,
            onClick = { onSelectScreen(AppScreen.AI_STYLIST) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.AI_STYLIST) Icons.Filled.TipsAndUpdates else Icons.Outlined.TipsAndUpdates,
                    contentDescription = "AI Stylist"
                )
            },
            label = {
                Text(
                    text = if (isFarsi) "مشاور استایل" else "AI Stylist",
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == AppScreen.AI_STYLIST) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF140D12),
                selectedTextColor = ChampagneGold,
                indicatorColor = RoseGoldPrimary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.LOOKBOOK,
            onClick = { onSelectScreen(AppScreen.LOOKBOOK) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.LOOKBOOK) Icons.Filled.CollectionsBookmark else Icons.Outlined.CollectionsBookmark,
                    contentDescription = "My Looks"
                )
            },
            label = {
                Text(
                    text = if (isFarsi) "پروهای من" else "My Looks",
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == AppScreen.LOOKBOOK) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF140D12),
                selectedTextColor = ChampagneGold,
                indicatorColor = RoseGoldPrimary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}
