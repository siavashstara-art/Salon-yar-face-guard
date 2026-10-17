package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BanootViewModel
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DeepBurgundy
import com.example.ui.theme.RoseGoldPrimary
import com.example.ui.theme.VelvetPlum

@Composable
fun AiStylistScreen(
    viewModel: BanootViewModel,
    modifier: Modifier = Modifier
) {
    val isFarsi by viewModel.isFarsi.collectAsState()
    val bodyShape by viewModel.stylistBodyShape.collectAsState()
    val venue by viewModel.stylistVenue.collectAsState()
    val season by viewModel.stylistSeason.collectAsState()
    val advice by viewModel.stylistAdvice.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        // Stylist Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = ChampagneGold.copy(alpha = 0.2f),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Stylist",
                                tint = ChampagneGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isFarsi) "مشاور هوشمند استایل عروس بانوت" else "Banoot AI Bridal Stylist",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChampagneGold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isFarsi) "تحلیل آناتومی اندام، سبک مراسم و انتخاب بی‌نقص‌ترین لباس عروس"
                    else "Anatomical body-shape matching & haute couture curation",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Questionnaire Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // 1. Body Shape
                Text(
                    text = if (isFarsi) "۱. فرم اندام شما چیست؟" else "1. What is your body silhouette?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val shapes = listOf(
                        "hourglass" to ("ساعت شنی (Hourglass)" to "Hourglass"),
                        "pear" to ("گلابی (Pear)" to "Pear"),
                        "rectangle" to ("مستطیلی (Rectangle)" to "Rectangle"),
                        "apple" to ("سیبی / پتیت (Apple/Petite)" to "Apple / Petite")
                    )
                    items(shapes) { (key, labels) ->
                        val selected = bodyShape == key
                        ChoiceChip(
                            title = if (isFarsi) labels.first else labels.second,
                            selected = selected,
                            onClick = { viewModel.updateStylistQuery(key, venue, season) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Venue
                Text(
                    text = if (isFarsi) "۲. لوکیشن و محل برگزاری جشن:" else "2. Wedding Venue:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val venues = listOf(
                        "ballroom" to ("تالار مجلل و لوکس" to "Grand Ballroom"),
                        "garden" to ("باغ عمارت و فضای باز" to "Garden & Villa"),
                        "beach" to ("ساحل و فرمالیته" to "Destination / Beach"),
                        "intimate" to ("مراسم عقد صمیمانه" to "Intimate Salon")
                    )
                    items(venues) { (key, labels) ->
                        val selected = venue == key
                        ChoiceChip(
                            title = if (isFarsi) labels.first else labels.second,
                            selected = selected,
                            onClick = { viewModel.updateStylistQuery(bodyShape, key, season) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Season
                Text(
                    text = if (isFarsi) "۳. فصل برگزاری مراسم:" else "3. Wedding Season:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val seasons = listOf(
                        "spring_summer" to ("بهار و تابستان" to "Spring / Summer"),
                        "fall_winter" to ("پاییز و زمستان" to "Fall / Winter")
                    )
                    items(seasons) { (key, labels) ->
                        val selected = season == key
                        ChoiceChip(
                            title = if (isFarsi) labels.first else labels.second,
                            selected = selected,
                            onClick = { viewModel.updateStylistQuery(bodyShape, venue, key) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI Stylist Result Report
        if (advice != null) {
            val rec = advice!!
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = if (isFarsi) "گزارش مشاور اختصاصی استایل بانوت" else "Banoot Atelier Stylist Report",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ChampagneGold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Golden Tip Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepBurgundy.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.4f))
                ) {
                    Row(modifier = Modifier.padding(14.dp)) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Tip",
                            tint = ChampagneGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = rec.generalTipFa,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Recommended Silhouettes Card
                AdviceCard(
                    icon = Icons.Default.Checkroom,
                    title = if (isFarsi) "بهترین مدل‌های لباس عروس برای شما:" else "Recommended Silhouettes:",
                    content = rec.bestSilhouettesFa.joinToString("\n• ", prefix = "• ")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Veil Advice Card
                AdviceCard(
                    icon = Icons.Outlined.Face,
                    title = if (isFarsi) "راهنمای تور و قد دنباله:" else "Veil & Train Length:",
                    content = rec.veilRecommendationFa
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Jewelry & Accessories Card
                AdviceCard(
                    icon = Icons.Default.Diamond,
                    title = if (isFarsi) "اکسسوری، تاج و جواهرات متناسب:" else "Jewelry & Accessories:",
                    content = rec.jewelryRecommendationFa
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Fabric Advice Card
                AdviceCard(
                    icon = Icons.Outlined.Texture,
                    title = if (isFarsi) "جنس پارچه ایده‌آل برای لوکیشن و فصل:" else "Ideal Fabric & Texture:",
                    content = rec.fabricAdviceFa
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Button: Go to fitting room with top recommended dress
                Button(
                    onClick = {
                        val targetDress = when (bodyShape) {
                            "hourglass" -> viewModel.dresses.find { it.category == com.example.model.DressCategory.MERMAID }
                            "pear" -> viewModel.dresses.find { it.category == com.example.model.DressCategory.PRINCESS }
                            "rectangle" -> viewModel.dresses.find { it.category == com.example.model.DressCategory.CORSET_LINGERIE }
                            else -> viewModel.dresses.find { it.category == com.example.model.DressCategory.A_LINE }
                        } ?: viewModel.dresses.first()

                        viewModel.selectDress(targetDress)
                        viewModel.navigateTo(com.example.ui.AppScreen.TRY_ON)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoseGoldPrimary,
                        contentColor = Color(0xFF140D12)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Try")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFarsi) "پرو آنلاین مدل پیشنهادی هوش مصنوعی" else "Try On Recommended Gown",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AdviceCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = RoseGoldPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 19.sp
            )
        }
    }
}
