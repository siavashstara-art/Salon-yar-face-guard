package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.model.BridalDress
import com.example.model.DressCategory
import com.example.ui.BanootViewModel
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DeepBurgundy
import com.example.ui.theme.RoseGoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BridalCatalogScreen(
    viewModel: BanootViewModel,
    modifier: Modifier = Modifier
) {
    val dresses = viewModel.dresses
    val isFarsi by viewModel.isFarsi.collectAsState()
    val inspectedDress by viewModel.inspectedDress.collectAsState()

    var selectedCategory by remember { mutableStateOf(DressCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = remember(selectedCategory, searchQuery) {
        dresses.filter { dress ->
            val matchesCategory = selectedCategory == DressCategory.ALL || dress.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                    dress.titleFa.contains(searchQuery, ignoreCase = true) ||
                    dress.titleEn.contains(searchQuery, ignoreCase = true) ||
                    dress.fabricTypeFa.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 90.dp)
    ) {
        // Top Search & Title
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isFarsi) "کاتالوگ کالکشن لباس عروس و مزون بانوت" else "Banoot Atelier Bridal Collection",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ChampagneGold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isFarsi) "طراحی‌های دست‌دوز، کرست‌های ارگونومیک و دانتل‌های فرانسوی" else "Handcrafted haute couture, corsetry & French lace",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = ChampagneGold)
                },
                placeholder = {
                    Text(
                        text = if (isFarsi) "جستجوی مدل، جنس پارچه، برش ماهی یا پرنسسی..." else "Search gowns, silhouettes or fabrics...",
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("catalog_search_bar"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ChampagneGold,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                singleLine = true
            )
        }

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

        Spacer(modifier = Modifier.height(8.dp))

        // Catalog List
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredList) { dress ->
                CatalogItemCard(
                    dress = dress,
                    isFarsi = isFarsi,
                    onInspect = { viewModel.openDressDetail(dress) },
                    onTryOn = { viewModel.tryOnFromCatalog(dress) }
                )
            }
        }
    }

    // Inspect Details Dialog
    if (inspectedDress != null) {
        val dress = inspectedDress!!
        Dialog(
            onDismissRequest = { viewModel.closeDressDetail() },
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
                        Text(
                            text = if (isFarsi) "مشخصات آتلیه‌ای لباس عروس" else "Gown Haute Couture Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ChampagneGold
                        )
                        IconButton(onClick = { viewModel.closeDressDetail() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Large High-Res Dress Photo
                    AsyncImage(
                        model = dress.imageRes,
                        contentDescription = dress.titleEn,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isFarsi) dress.titleFa else dress.titleEn,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isFarsi) dress.descriptionFa else dress.descriptionEn,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Specs Table
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            SpecRow(
                                title = if (isFarsi) "برش و سیلوئت:" else "Silhouette:",
                                value = if (isFarsi) dress.silhouetteFa else dress.silhouetteEn
                            )
                            SpecRow(
                                title = if (isFarsi) "ترکیب پارچه:" else "Fabric:",
                                value = if (isFarsi) dress.fabricTypeFa else dress.fabricTypeEn
                            )
                            if (dress.trainLengthCm > 0) {
                                SpecRow(
                                    title = if (isFarsi) "طول دنباله:" else "Train Length:",
                                    value = "${dress.trainLengthCm} cm"
                                )
                            }
                            SpecRow(
                                title = if (isFarsi) "مناسب فرم اندام:" else "Ideal for:",
                                value = dress.suitableBodyShapes.joinToString("، ")
                            )
                            SpecRow(
                                title = if (isFarsi) "قیمت اجاره:" else "Rental:",
                                value = if (isFarsi) "${dress.rentPriceToman / 1_000_000} میلیون تومان" else "$${dress.rentPriceToman / 100_000}"
                            )
                            SpecRow(
                                title = if (isFarsi) "قیمت خرید اختصاصی:" else "Purchase:",
                                value = if (isFarsi) "${dress.purchasePriceToman / 1_000_000} میلیون تومان" else "$${dress.purchasePriceToman / 100_000}"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Try On CTA
                    Button(
                        onClick = { viewModel.tryOnFromCatalog(dress) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoseGoldPrimary,
                            contentColor = Color(0xFF140D12)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Try on")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isFarsi) "پرو فوری این لباس در اتاق پرو هوشمند" else "Try On This Dress Now",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun CatalogItemCard(
    dress: BridalDress,
    isFarsi: Boolean,
    onInspect: () -> Unit,
    onTryOn: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onInspect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column {
            Box {
                AsyncImage(
                    model = dress.imageRes,
                    contentDescription = dress.titleEn,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )

                if (dress.isBestSeller) {
                    Surface(
                        color = DeepBurgundy,
                        shape = RoundedCornerShape(bottomStart = 8.dp),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = if (isFarsi) "کالکشن ویژه بانوت" else "Banoot Signature",
                            color = ChampagneGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isFarsi) dress.titleFa else dress.titleEn,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isFarsi) dress.fabricTypeFa else dress.fabricTypeEn,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isFarsi) dress.descriptionFa else dress.descriptionEn,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isFarsi) "قیمت اجاره برای مراسم:" else "Rental Rate:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isFarsi) "${dress.rentPriceToman / 1_000_000} میلیون تومان" else "$${dress.rentPriceToman / 100_000}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = ChampagneGold
                        )
                    }

                    Button(
                        onClick = onTryOn,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoseGoldPrimary.copy(alpha = 0.2f),
                            contentColor = RoseGoldPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Try on", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isFarsi) "پرو لباس" else "Try On",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpecRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}
