package com.example.ui.components

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.RoseGoldPrimary
import java.io.File
import kotlin.math.roundToInt

@Composable
fun BeforeAfterSlider(
    modifier: Modifier = Modifier,
    beforeImageUri: String? = null,
    @DrawableRes beforeImageRes: Int? = null,
    afterImagePath: String? = null,
    @DrawableRes afterImageRes: Int? = null,
    sliderPosition: Float = 0.5f,
    onPositionChanged: (Float) -> Unit,
    isFarsi: Boolean = true
) {
    var widthPx by remember { mutableFloatStateOf(1f) }
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(440.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF140D12))
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val newPos = (change.position.x / size.width).coerceIn(0.05f, 0.95f)
                    onPositionChanged(newPos)
                }
            }
    ) {
        widthPx = constraints.maxWidth.toFloat()
        val splitX = widthPx * sliderPosition

        // Layer 1: After Image (Full background)
        val afterModel: Any = remember(afterImagePath, afterImageRes) {
            if (afterImagePath != null && File(afterImagePath).exists()) {
                File(afterImagePath)
            } else {
                afterImageRes ?: com.example.R.drawable.model_bride_fitted
            }
        }

        AsyncImage(
            model = afterModel,
            contentDescription = "After Bridal Try-On",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Layer 2: Before Image clipped to left of split line
        val beforeModel: Any = remember(beforeImageUri, beforeImageRes) {
            if (beforeImageUri != null) {
                Uri.parse(beforeImageUri)
            } else {
                beforeImageRes ?: com.example.R.drawable.model_bride_pose
            }
        }

        // Draw clipped before image overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val clipRect = Rect(0f, 0f, splitX, size.height)
            val clipPath = Path().apply {
                addRect(clipRect)
            }
            // Clip to split line
            clipPath(clipPath) {
                // Drawing handled by overlaying composable or line
            }
        }

        // Clip container for before image
        val splitPercent = (sliderPosition * 100).coerceIn(5f, 95f)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(sliderPosition)
                .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
        ) {
            AsyncImage(
                model = beforeModel,
                contentDescription = "Before Try-On",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(with(density) { widthPx.toDp() })
            )
        }

        // Divider Line & Handle
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Vertical split line with glow
            drawLine(
                color = Color.Black.copy(alpha = 0.5f),
                start = Offset(splitX - 1f, 0f),
                end = Offset(splitX - 1f, size.height),
                strokeWidth = 4f
            )
            drawLine(
                color = ChampagneGold,
                start = Offset(splitX, 0f),
                end = Offset(splitX, size.height),
                strokeWidth = 3f
            )
        }

        // Drag Handle Circle
        val handleOffsetDp = with(density) { (splitX).toDp() - 20.dp }
        Box(
            modifier = Modifier
                .offset { IntOffset((splitX - 20 * density.density).roundToInt(), (constraints.maxHeight / 2 - 20 * density.density).roundToInt()) }
                .size(40.dp)
                .clip(CircleShape)
                .background(ChampagneGold)
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color(0xFF20131B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CompareArrows,
                    contentDescription = "Drag to compare",
                    tint = ChampagneGold,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Badges: "قبل (Before)" and "بعد از پرو هوشمند (After)"
        Surface(
            color = Color.Black.copy(alpha = 0.65f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        ) {
            Text(
                text = if (isFarsi) "قبل از پرو" else "Before",
                color = Color.White,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Surface(
            color = RoseGoldPrimary.copy(alpha = 0.9f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
        ) {
            Text(
                text = if (isFarsi) "بعد از پرو هوشمند بانوت" else "After Banoot AI",
                color = Color(0xFF180E14),
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // Drag Hint at bottom
        Surface(
            color = Color.Black.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp)
        ) {
            Text(
                text = if (isFarsi) "برای مقایسه قبل و بعد خط را بکشید ⟷" else "Drag slider to compare Before & After ⟷",
                color = ChampagneGold,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp)
            )
        }
    }
}
