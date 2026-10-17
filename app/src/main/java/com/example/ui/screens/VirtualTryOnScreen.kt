package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.R
import com.example.model.*
import com.example.ui.BanootViewModel
import com.example.ui.components.BeforeAfterSlider
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirtualTryOnScreen(
    viewModel: BanootViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.tryOnState.collectAsState()
    val isFarsi by viewModel.isFarsi.collectAsState()
    val dresses = viewModel.dresses
    val models = viewModel.modelPoses

    // Photo picker launcher with ZERO permissions
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        viewModel.setCustomPhotoUri(uri)
    }

    var selectedCategory by remember { mutableStateOf(DressCategory.ALL) }
    var showAdjustments by remember { mutableStateOf(false) }

    val filteredDresses = remember(selectedCategory) {
        if (selectedCategory == DressCategory.ALL) dresses
        else dresses.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        // Atelier Banner & Live Status
        AtelierHeader(isFarsi = isFarsi)

        Spacer(modifier = Modifier.height(16.dp))

        // Section 1: Model & Bride Photo Selection
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isFarsi) "۱. انتخاب مدل یا بارگذاری عکس شما" else "1. Choose Model or Upload Photo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ChampagneGold.copy(alpha = 0.2f),
                            contentColor = ChampagneGold
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("upload_photo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoCamera,
                            contentDescription = "Upload",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isFarsi) "آپلود عکسم" else "Upload Photo",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Custom photo active banner or model chips
                if (state.isCustomPhotoActive && state.customPhotoUri != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(RoseGoldPrimary.copy(alpha = 0.15f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = state.customPhotoUri,
                            contentDescription = "Custom Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isFarsi) "عکس اختصاصی شما بارگذاری شد" else "Your custom photo is active",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isFarsi) "پرو لباس مستقیماً روی فیگور شما انجام می‌شود" else "Dress will warp directly onto your photo",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { viewModel.setUsePresetModel() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Use Preset Model",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                } else {
                    // Models Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(models) { model ->
                            val isSelected = !state.isCustomPhotoActive && state.selectedModel.id == model.id
                            ModelCardItem(
                                model = model,
                                isSelected = isSelected,
                                isFarsi = isFarsi,
                                onClick = { viewModel.selectModel(model) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 2: Dress Category Filter & Selection
        Text(
            text = if (isFarsi) "۲. انتخاب لباس عروس و مزون" else "2. Select Wedding Dress & Couture",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(DressCategory.values()) { category ->
                val selected = selectedCategory == category
                FilterChip(
                    selected = selected,
                    onClick = { selectedCategory = category },
                    label = {
                        Text(
                            text = if (isFarsi) category.titleFa else category.titleEn,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dresses Carousel
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filteredDresses) { dress ->
                val isSelected = state.selectedDress.id == dress.id
                DressCardItem(
                    dress = dress,
                    isSelected = isSelected,
                    isFarsi = isFarsi,
                    onClick = { viewModel.selectDress(dress) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 3: Advanced Fitting Adjustments (Expandable)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAdjustments = !showAdjustments },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Tune,
                            contentDescription = "Tune",
                            tint = ChampagneGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isFarsi) "تنظیمات پیشرفته فیتینگ، تور و نورپردازی" else "Advanced Fitting, Veil & Lighting",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Icon(
                        imageVector = if (showAdjustments) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                AnimatedVisibility(visible = showAdjustments) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        // Waist adjustment
                        Text(
                            text = if (isFarsi) "سایز و تنگی کرست بالاتنه: ${state.waistAdjustment}%"
                            else "Corset & Waist Tightness: ${state.waistAdjustment}%",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Slider(
                            value = state.waistAdjustment.toFloat(),
                            onValueChange = { viewModel.updateWaistAdjustment(it.toInt()) },
                            valueRange = -10f..10f,
                            steps = 9,
                            colors = SliderDefaults.colors(
                                thumbColor = ChampagneGold,
                                activeTrackColor = ChampagneGold
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Veil Style Selector
                        Text(
                            text = if (isFarsi) "مدل و طول تور سر:" else "Bridal Veil Style:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(VeilStyle.values()) { veil ->
                                val sel = state.veilStyle == veil
                                ChoiceChip(
                                    title = if (isFarsi) veil.titleFa else veil.titleEn,
                                    selected = sel,
                                    onClick = { viewModel.updateVeilStyle(veil) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Tiara Style Selector
                        Text(
                            text = if (isFarsi) "تاج و اکسسوری سر:" else "Tiara & Headpiece:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(TiaraStyle.values()) { tiara ->
                                val sel = state.tiaraStyle == tiara
                                ChoiceChip(
                                    title = if (isFarsi) tiara.titleFa else tiara.titleEn,
                                    selected = sel,
                                    onClick = { viewModel.updateTiaraStyle(tiara) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Lighting Preset
                        Text(
                            text = if (isFarsi) "نورپردازی محیط آتلیه:" else "Studio Atmosphere & Lighting:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(LightingPreset.values()) { preset ->
                                val sel = state.lightingPreset == preset
                                ChoiceChip(
                                    title = if (isFarsi) preset.titleFa else preset.titleEn,
                                    selected = sel,
                                    onClick = { viewModel.updateLightingPreset(preset) }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main CTA Button: Start Virtual Try-On
        Button(
            onClick = { viewModel.startVirtualTryOn() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp)
                .testTag("start_try_on_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = RoseGoldPrimary,
                contentColor = Color(0xFF140D12)
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Sparkle",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isFarsi) "پرو هوشمند لباس عروس (${state.selectedDress.titleFa})"
                    else "Try On Gown with Banoot AI",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick tip info box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = "Info",
                tint = ChampagneGold,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isFarsi) "موتور هوش مصنوعی بانوت فرم اندام، نور و چین پارچه را شبیه‌سازی می‌کند"
                else "Banoot AI accurately simulates body contours, fabric drape and satin reflections",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }

    // Processing Dialog
    if (state.isProcessing) {
        Dialog(
            onDismissRequest = { /* Cannot dismiss while processing */ },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(RoseGoldPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { state.progressPercent },
                            color = ChampagneGold,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(60.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Processing",
                            tint = ChampagneGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (isFarsi) "در حال پردازش پرو هوشمند عروس" else "Banoot AI Fitting in Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = state.currentStepText,
                        fontSize = 13.sp,
                        color = ChampagneGold,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = { state.progressPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = RoseGoldPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "${(state.progressPercent * 100).toInt()}%",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Result BottomSheet / Dialog with Interactive Before/After Split Slider
    if (state.showResultSheet && state.latestResult != null) {
        val result = state.latestResult!!
        Dialog(
            onDismissRequest = { viewModel.dismissResultSheet() },
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
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isFarsi) "نتیجه پرو هوشمند لباس عروس" else "Bridal Virtual Try-On Result",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = ChampagneGold
                            )
                            Text(
                                text = if (isFarsi) result.dressTitleFa else result.dressTitleEn,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(onClick = { viewModel.dismissResultSheet() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Fit Score Badge
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(RoseGoldPrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified Fit",
                                tint = RoseGoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isFarsi) "شاخص تطابق و زیبایی اندام:" else "Silhouette Harmony Score:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = "${result.fitScore}%",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ChampagneGold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Interactive Before / After Split Slider
                    BeforeAfterSlider(
                        beforeImageUri = result.beforeImagePath,
                        beforeImageRes = result.beforeImageRes,
                        afterImagePath = result.resultImagePath,
                        afterImageRes = result.resultFallbackRes,
                        sliderPosition = state.sliderPosition,
                        onPositionChanged = { viewModel.updateSliderPosition(it) },
                        isFarsi = isFarsi,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // User Notes TextField
                    var noteText by remember { mutableStateOf(result.notes) }
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = {
                            noteText = it
                            viewModel.updateNotes(it)
                        },
                        label = {
                            Text(if (isFarsi) "یادداشت شخصی شما (مثلا: مناسب برای عکاسی تالار)" else "Personal Fitting Notes")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ChampagneGold,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.saveCurrentLookWithNotes(noteText)
                                viewModel.dismissResultSheet()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RoseGoldPrimary,
                                contentColor = Color(0xFF140D12)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Bookmark, contentDescription = "Save")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isFarsi) "ذخیره در دفترچه پروها" else "Save to My Looks",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.showMessage(if (isFarsi) "لینک نتیجه پرو برای اشتراک آماده شد" else "Look ready to share")
                            },
                            modifier = Modifier.height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ChampagneGold)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = ChampagneGold)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun AtelierHeader(isFarsi: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DeepBurgundy,
                        VelvetPlum,
                        Color(0xFF140D12)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = ChampagneGold.copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Diamond,
                            contentDescription = "Banoot Atelier",
                            tint = ChampagneGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "BANOOT ATELIER",
                    letterSpacing = 2.sp,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChampagneGold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isFarsi) "اتاق پرو هوشمند لباس عروس و مزون" else "AI Bridal & Haute Couture Fitting Room",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = OnDarkText
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isFarsi) "شبیه‌سازی دقیق فیتینگ، تنخور پارچه‌های ابریشمی و کرست" else "Virtual try-on with precision drape & silhouette mapping",
                fontSize = 12.sp,
                color = RoseGoldLight
            )
        }
    }
}

@Composable
fun ModelCardItem(
    model: ModelPose,
    isSelected: Boolean,
    isFarsi: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(115.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) RoseGoldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isSelected) BorderStroke(2.dp, ChampagneGold) else null
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = model.imageRes,
                contentDescription = model.nameEn,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isFarsi) model.nameFa else model.nameEn,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
            Text(
                text = "${model.heightCm} cm",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DressCardItem(
    dress: BridalDress,
    isSelected: Boolean,
    isFarsi: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .clickable { onClick() }
            .testTag("dress_card_${dress.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DeepBurgundy.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) ChampagneGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    ) {
        Column {
            Box {
                AsyncImage(
                    model = dress.imageRes,
                    contentDescription = dress.titleEn,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                )

                if (dress.isBestSeller) {
                    Surface(
                        color = DeepBurgundy,
                        shape = RoundedCornerShape(bottomStart = 8.dp),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = if (isFarsi) "محبوب‌ترین" else "Best Seller",
                            color = ChampagneGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                if (isSelected) {
                    Surface(
                        color = ChampagneGold,
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(6.dp)
                            .size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color(0xFF140D12),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = if (isFarsi) dress.titleFa else dress.titleEn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isFarsi) dress.silhouetteFa else dress.silhouetteEn,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isFarsi) "اجاره:" else "Rent:",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isFarsi) "${dress.rentPriceToman / 1_000_000} م ت" else "$${dress.rentPriceToman / 100_000}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChampagneGold
                    )
                }
            }
        }
    }
}

@Composable
fun ChoiceChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        border = if (selected) BorderStroke(1.dp, ChampagneGold) else null,
        modifier = Modifier.height(34.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
