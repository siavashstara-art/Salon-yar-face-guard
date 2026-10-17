package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.TryOnResult
import com.example.ui.BanootViewModel
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DeepBurgundy
import com.example.ui.theme.RoseGoldPrimary
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SavedLooksScreen(
    viewModel: BanootViewModel,
    modifier: Modifier = Modifier
) {
    val isFarsi by viewModel.isFarsi.collectAsState()
    val savedLooks by viewModel.savedLooks.collectAsState()
    val isComparing by viewModel.isComparing.collectAsState()
    val comparisonA by viewModel.comparisonA.collectAsState()
    val comparisonB by viewModel.comparisonB.collectAsState()

    var selectedForCompareA by remember { mutableStateOf<TryOnResult?>(null) }
    var selectedForCompareB by remember { mutableStateOf<TryOnResult?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 90.dp)
    ) {
        // Header
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isFarsi) "دفترچه پروهای ذخیره شده من" else "My Saved Bridal Fittings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ChampagneGold
                    )
                    Text(
                        text = if (isFarsi) "مجموعاً ${savedLooks.size} پرو لباس عروس ذخیره شده" else "${savedLooks.size} looks saved",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (savedLooks.size >= 2) {
                    Button(
                        onClick = {
                            if (savedLooks.size >= 2) {
                                viewModel.startComparing(savedLooks[0], savedLooks[1])
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ChampagneGold.copy(alpha = 0.2f),
                            contentColor = ChampagneGold
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Compare, contentDescription = "Compare", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isFarsi) "مقایسه دو مدل" else "Compare 2",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        if (savedLooks.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = RoseGoldPrimary.copy(alpha = 0.15f),
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Checkroom,
                                contentDescription = "Empty",
                                tint = RoseGoldPrimary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (isFarsi) "هنوز لباسی پرو نکرده‌اید" else "No Saved Fittings Yet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isFarsi) "به اتاق پرو بروید، لباس عروس دلخواه‌تان را امتحان کنید و در این بخش ذخیره نمایید."
                        else "Visit the Virtual Fitting Room, try on gowns, and save your favorite looks here.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.navigateTo(com.example.ui.AppScreen.TRY_ON) },
                        colors = ButtonDefaults.buttonColors(containerColor = RoseGoldPrimary, contentColor = Color(0xFF140D12)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isFarsi) "ورود به اتاق پرو هوشمند" else "Go to Fitting Room",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            // List of Saved Looks
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(savedLooks) { look ->
                    SavedLookCard(
                        look = look,
                        isFarsi = isFarsi,
                        onDelete = { viewModel.deleteSavedLook(look.id) },
                        onSelectForCompare = {
                            if (selectedForCompareA == null) {
                                selectedForCompareA = look
                                viewModel.showMessage(if (isFarsi) "مدل اول انتخاب شد؛ مدل دوم را انتخاب کنید" else "First look selected; pick second")
                            } else if (selectedForCompareB == null && selectedForCompareA?.id != look.id) {
                                selectedForCompareB = look
                                viewModel.startComparing(selectedForCompareA!!, look)
                                selectedForCompareA = null
                                selectedForCompareB = null
                            }
                        }
                    )
                }
            }
        }
    }

    // Side by Side Comparison Modal
    if (isComparing && comparisonA != null && comparisonB != null) {
        val lookA = comparisonA!!
        val lookB = comparisonB!!

        Dialog(
            onDismissRequest = { viewModel.stopComparing() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 28.dp),
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isFarsi) "مقایسه هم‌زمان دو لباس عروس پرو شده" else "Side-by-Side Gown Comparison",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ChampagneGold
                        )
                        IconButton(onClick = { viewModel.stopComparing() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Look A
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isFarsi) lookA.dressTitleFa else lookA.dressTitleEn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                color = ChampagneGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val imageModelA: Any = if (File(lookA.resultImagePath).exists()) File(lookA.resultImagePath) else lookA.resultFallbackRes
                            AsyncImage(
                                model = imageModelA,
                                contentDescription = lookA.dressTitleEn,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "فیت: ${lookA.fitScore}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = RoseGoldPrimary
                            )
                        }

                        // Look B
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isFarsi) lookB.dressTitleFa else lookB.dressTitleEn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                color = ChampagneGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val imageModelB: Any = if (File(lookB.resultImagePath).exists()) File(lookB.resultImagePath) else lookB.resultFallbackRes
                            AsyncImage(
                                model = imageModelB,
                                contentDescription = lookB.dressTitleEn,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "فیت: ${lookB.fitScore}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = RoseGoldPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.stopComparing() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = RoseGoldPrimary, contentColor = Color(0xFF140D12)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = if (isFarsi) "بستن مقایسه" else "Close Comparison", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SavedLookCard(
    look: TryOnResult,
    isFarsi: Boolean,
    onDelete: () -> Unit,
    onSelectForCompare: () -> Unit
) {
    val dateStr = remember(look.timestamp) {
        val sdf = SimpleDateFormat("yyyy/MM/dd - HH:mm", Locale.getDefault())
        sdf.format(Date(look.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("saved_look_card_${look.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isFarsi) look.dressTitleFa else look.dressTitleEn,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = dateStr,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Image Preview
            val imageModel: Any = remember(look.resultImagePath, look.resultFallbackRes) {
                if (File(look.resultImagePath).exists()) File(look.resultImagePath) else look.resultFallbackRes
            }

            AsyncImage(
                model = imageModel,
                contentDescription = look.dressTitleEn,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Fit Score & Notes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isFarsi) "شاخص زیبایی و فیت: ${look.fitScore}%" else "Fit Harmony: ${look.fitScore}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ChampagneGold
                    )
                }

                OutlinedButton(
                    onClick = onSelectForCompare,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    border = BorderStroke(1.dp, RoseGoldPrimary)
                ) {
                    Text(
                        text = if (isFarsi) "انتخاب برای مقایسه" else "Compare",
                        fontSize = 11.sp,
                        color = RoseGoldPrimary
                    )
                }
            }

            if (look.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = look.notes,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
