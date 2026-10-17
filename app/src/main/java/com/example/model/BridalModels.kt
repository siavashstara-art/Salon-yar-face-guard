package com.example.model

import androidx.annotation.DrawableRes
import com.example.R

enum class DressCategory(val titleFa: String, val titleEn: String) {
    ALL("همه مدل‌ها", "All Collections"),
    PRINCESS("پرنسسی سلطنتی", "Princess Ballgown"),
    MERMAID("ماهی و شاین", "Mermaid & Shine"),
    A_LINE("ماکسی و ای‌لاین", "A-Line Romance"),
    FORMALITE("فرمالیته و بوهو", "Formalite & Boho"),
    CORSET_LINGERIE("لانژری و کرست", "Corset & Lingerie"),
    EVENING("عقد و شب", "Evening & Reception")
}

data class BridalDress(
    val id: String,
    val titleFa: String,
    val titleEn: String,
    val category: DressCategory,
    val descriptionFa: String,
    val descriptionEn: String,
    val fabricTypeFa: String,
    val fabricTypeEn: String,
    val silhouetteFa: String,
    val silhouetteEn: String,
    val purchasePriceToman: Long,
    val rentPriceToman: Long,
    @DrawableRes val imageRes: Int,
    val trainLengthCm: Int,
    val isBestSeller: Boolean = false,
    val tags: List<String> = emptyList(),
    val suitableBodyShapes: List<String> = emptyList()
)

data class ModelPose(
    val id: String,
    val nameFa: String,
    val nameEn: String,
    val bodyTypeFa: String,
    val bodyTypeEn: String,
    @DrawableRes val imageRes: Int,
    val heightCm: Int
)

enum class VeilStyle(val titleFa: String, val titleEn: String) {
    NONE("بدون تور", "No Veil"),
    FRENCH_BLUSHER("تور کوتاه فرانسوی", "French Blusher (60cm)"),
    CHAPEL_LACE("تور چپل حاشیه‌دوزی", "Chapel Lace (2.5m)"),
    ROYAL_CATHEDRAL("تور سلطنتی ۳ متری", "Royal Cathedral (3.5m)")
}

enum class TiaraStyle(val titleFa: String, val titleEn: String) {
    NONE("بدون تاج", "No Tiara"),
    ROYAL_CROWN("تاج سلطنتی کریستال", "Royal Crystal Crown"),
    PEARL_HEADBAND("ریسه مروارید باروک", "Baroque Pearl Headband"),
    DIAMOND_DIADEM("نیم‌تاج الماس ظریف", "Delicate Diamond Diadem")
}

enum class LightingPreset(val titleFa: String, val titleEn: String) {
    GRAND_BALLROOM("تالار مجلل و لوستر", "Grand Ballroom"),
    STUDIO_SOFT("آتلیه عروس نور گرم", "Warm Studio"),
    GARDEN_GOLDEN_HOUR("باغ عمارت فرمالیته", "Sunset Garden"),
    HIGH_FASHION("نور ادیتوریال مدرن", "Editorial Spotlight")
}

data class FittingConfig(
    val dress: BridalDress,
    val modelPose: ModelPose?,
    val customUserPhotoUri: String? = null,
    val waistAdjustmentPercent: Int = 0, // -10% to +10%
    val bustAdjustmentPercent: Int = 0,
    val veilStyle: VeilStyle = VeilStyle.CHAPEL_LACE,
    val tiaraStyle: TiaraStyle = TiaraStyle.ROYAL_CROWN,
    val lightingPreset: LightingPreset = LightingPreset.GRAND_BALLROOM
)

data class TryOnResult(
    val id: String,
    val dressId: String,
    val dressTitleFa: String,
    val dressTitleEn: String,
    val modelId: String?,
    val isCustomPhoto: Boolean,
    val resultImagePath: String,
    val beforeImagePath: String?,
    @DrawableRes val beforeImageRes: Int?,
    @DrawableRes val resultFallbackRes: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val fitScore: Int = 96,
    val notes: String = "",
    val config: FittingConfig
)

data class StylistAdvice(
    val bodyShapeFa: String,
    val bodyShapeEn: String,
    val bestSilhouettesFa: List<String>,
    val veilRecommendationFa: String,
    val jewelryRecommendationFa: String,
    val fabricAdviceFa: String,
    val generalTipFa: String
)
