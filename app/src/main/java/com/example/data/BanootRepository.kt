package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.R
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class BanootRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("banoot_atelier_prefs", Context.MODE_PRIVATE)

    // Preloaded Model Poses
    val modelPoses: List<ModelPose> = listOf(
        ModelPose(
            id = "pose_standard",
            nameFa = "مدل آتلیه بانوت (استاندارد)",
            nameEn = "Banoot Studio Model (Standard)",
            bodyTypeFa = "اندام متناسب و کشیده (Hourglass)",
            bodyTypeEn = "Hourglass & Tall",
            imageRes = R.drawable.model_bride_pose,
            heightCm = 175
        ),
        ModelPose(
            id = "pose_petite",
            nameFa = "مدل پتیت و ظریف",
            nameEn = "Petite Model",
            bodyTypeFa = "ظریف و مینیاتوری",
            bodyTypeEn = "Petite & Slender",
            imageRes = R.drawable.model_bride_pose,
            heightCm = 163
        ),
        ModelPose(
            id = "pose_curvy",
            nameFa = "مدل با انحنای زنانه (Curvy)",
            nameEn = "Curvy Model",
            bodyTypeFa = "انحنای کلاسیک و کمر باریک",
            bodyTypeEn = "Curvy & Pear",
            imageRes = R.drawable.model_bride_pose,
            heightCm = 170
        )
    )

    // Curated Wedding Gowns & Couture Catalog
    val dresses: List<BridalDress> = listOf(
        BridalDress(
            id = "dress_royal_princess",
            titleFa = "لباس عروس پرنسسی الیزابت",
            titleEn = "Royal Princess Elizabeth Ballgown",
            category = DressCategory.PRINCESS,
            descriptionFa = "شاهکار مزون بانوت؛ دامن تافته چندلایه و ارگانزا با بالاتنه دست‌دوز مروارید و نگین‌های درخشان سواروسکی، یقه دلبری و تور پرنسسی با شکوه.",
            descriptionEn = "Banoot Atelier masterpiece; multi-layered taffeta and organza ballgown with hand-sewn Swarovski crystal and pearl corset bodice, sweetheart neckline.",
            fabricTypeFa = "تافته ایتالیایی، دانتل فرانسه و ارگانزا شاین",
            fabricTypeEn = "Italian Taffeta, French Lace & Shimmer Organza",
            silhouetteFa = "پرنسسی باشکوه (Ballgown)",
            silhouetteEn = "Royal Ballgown",
            purchasePriceToman = 68_000_000,
            rentPriceToman = 24_000_000,
            imageRes = R.drawable.bridal_ballgown,
            trainLengthCm = 280,
            isBestSeller = true,
            tags = listOf("پرنسسی", "مجلل", "مرواریددوزی", "سواروسکی"),
            suitableBodyShapes = listOf("ساعت شنی", "گلابی", "مستطیلی", "سیبی")
        ),
        BridalDress(
            id = "dress_mermaid_serena",
            titleFa = "لباس عروس ماهی سرنا دایموند",
            titleEn = "Mermaid Serena Diamond Gown",
            category = DressCategory.MERMAID,
            descriptionFa = "طراحی جسورانه و فوق‌العاده شیک با برش اسلیم فیت که خطوط اندام را برجسته می‌سازد. پارچه شاین فرانسه با دنباله توری دراماتیک ۲.۵ متری.",
            descriptionEn = "Glamorous mermaid silhouette accentuating feminine curves with fine French shimmer lace and an illusion back with cathedral lace train.",
            fabricTypeFa = "دانتل شاین فرانسه، کرپ سیلک و گیپور",
            fabricTypeEn = "French Shimmer Lace, Silk Crepe & Guipure",
            silhouetteFa = "برش ماهی اسلیم (Mermaid Silhouette)",
            silhouetteEn = "Mermaid Cut",
            purchasePriceToman = 55_000_000,
            rentPriceToman = 19_000_000,
            imageRes = R.drawable.bridal_mermaid,
            trainLengthCm = 250,
            isBestSeller = true,
            tags = listOf("مدل ماهی", "شاین", "اسلیم فیت", "لاکچری"),
            suitableBodyShapes = listOf("ساعت شنی", "مستطیلی")
        ),
        BridalDress(
            id = "dress_aline_juliette",
            titleFa = "لباس عروس ای‌لاین ژولیت رمانتیک",
            titleEn = "A-Line Juliette Romance Gown",
            category = DressCategory.A_LINE,
            descriptionFa = "زیبایی کلاسیک و رمانتیک؛ مناسب تمام فرم‌های اندام با کمربند کریستالی و گل‌های برجسته سه بعدی روی دامن ارگانزا.",
            descriptionEn = "Timeless romantic A-line gown with 3D floral appliqués cascading down shimmering organza layers and delicate illusion cap sleeves.",
            fabricTypeFa = "حریر ارگانزا، دانتل مکرومه و میکادو",
            fabricTypeEn = "Silk Organza, Macrame Lace & Mikado",
            silhouetteFa = "ای‌لاین کلاسیک (A-Line)",
            silhouetteEn = "Classic A-Line",
            purchasePriceToman = 48_000_000,
            rentPriceToman = 17_000_000,
            imageRes = R.drawable.bridal_ballgown,
            trainLengthCm = 180,
            isBestSeller = false,
            tags = listOf("ای لاین", "رمانتیک", "گل سه بعدی", "سبک"),
            suitableBodyShapes = listOf("گلابی", "سیبی", "ساعت شنی", "مثلث معکوس")
        ),
        BridalDress(
            id = "dress_boho_formalite",
            titleFa = "لباس فرمالیته و عکاسی بوهو آمور",
            titleEn = "Boho Amour Formalite & Pre-Wedding Dress",
            category = DressCategory.FORMALITE,
            descriptionFa = "مخصوص عکاسی فرمالیته در طبیعت، کویر یا عمارت؛ پارچه ابریشم و حریر بسیار سبک با آستین‌های افتاده کلوش و چاک ظریف.",
            descriptionEn = "Ethereal bohemian pre-wedding gown crafted from feather-light silk chiffon, off-the-shoulder flutter sleeves and delicate side slit.",
            fabricTypeFa = "شیفون ۱۰۰٪ ابریشم طبیعی و توری کروشه",
            fabricTypeEn = "100% Pure Silk Chiffon & Crochet Lace",
            silhouetteFa = "آزاد و بوهیمیایی (Boho Chic)",
            silhouetteEn = "Bohemian Flowing",
            purchasePriceToman = 36_000_000,
            rentPriceToman = 12_000_000,
            imageRes = R.drawable.bridal_ballgown,
            trainLengthCm = 120,
            isBestSeller = false,
            tags = listOf("فرمالیته", "بوهو", "ابریشم", "عکاسی باغ"),
            suitableBodyShapes = listOf("ساعت شنی", "مستطیلی", "گلابی")
        ),
        BridalDress(
            id = "corset_banoot_bridal",
            titleFa = "کرست ابریشمی و لانژری عروس بانوت",
            titleEn = "Banoot Couture Silk & Pearl Corset",
            category = DressCategory.CORSET_LINGERIE,
            descriptionFa = "کالکشن اختصاصی لانژری و فنردوزی لوکس بانوت؛ کرست دست‌دوز با فنرهای استیل فرم‌دهنده، مرواریددوزی برجسته و ساتن رزگلد ایتالیایی.",
            descriptionEn = "Banoot Signature Couture Corset with steel boning architecture, hand-stitched freshwater pearls and Italian blush rose gold satin.",
            fabricTypeFa = "ساتن دوشس ایتالیا، گیپور شانتیلی و فنر استیل",
            fabricTypeEn = "Duchess Satin, Chantilly Lace & Boning",
            silhouetteFa = "کرست کالباسی ساعت‌شنی (Couture Corset)",
            silhouetteEn = "Hourglass Corset",
            purchasePriceToman = 18_500_000,
            rentPriceToman = 7_000_000,
            imageRes = R.drawable.lingerie_corset,
            trainLengthCm = 0,
            isBestSeller = true,
            tags = listOf("کرست", "لانژری عروس", "بانوت", "دست دوز"),
            suitableBodyShapes = listOf("ساعت شنی", "گلابی", "مستطیلی")
        ),
        BridalDress(
            id = "dress_reception_venus",
            titleFa = "لباس عقد و نامزدی ساتن ونوس",
            titleEn = "Venus Satin Reception & Engagement Gown",
            category = DressCategory.EVENING,
            descriptionFa = "پیراهن ماکسی شب و مراسم عقد از ساتن سنگین آمریکایی با درخشش ملایم، یقه دراپه و کمربند نگین‌دار رزگلد.",
            descriptionEn = "Lustrous American heavy satin gown with cowl neckline, crystal-encrusted waist sash and graceful drape for engagements and receptions.",
            fabricTypeFa = "ساتن ابریشم آمریکایی و سنگ‌دوزی کریستال",
            fabricTypeEn = "American Silk Satin & Crystal Embellishments",
            silhouetteFa = "ماکسی دراپه (Draped Maxi)",
            silhouetteEn = "Draped Evening Gown",
            purchasePriceToman = 29_000_000,
            rentPriceToman = 10_500_000,
            imageRes = R.drawable.bridal_mermaid,
            trainLengthCm = 80,
            isBestSeller = false,
            tags = listOf("لباس عقد", "نامزدی", "ساتن", "رزگلد"),
            suitableBodyShapes = listOf("ساعت شنی", "مستطیلی", "سیبی")
        )
    )

    private val _savedLooks = MutableStateFlow<List<TryOnResult>>(emptyList())
    val savedLooks: StateFlow<List<TryOnResult>> = _savedLooks.asStateFlow()

    init {
        loadSavedLooks()
    }

    fun saveLook(look: TryOnResult) {
        val updated = listOf(look) + _savedLooks.value.filter { it.id != look.id }
        _savedLooks.value = updated
        persistSavedLooks(updated)
    }

    fun deleteLook(id: String) {
        val updated = _savedLooks.value.filter { it.id != id }
        _savedLooks.value = updated
        persistSavedLooks(updated)
    }

    private fun loadSavedLooks() {
        val jsonString = prefs.getString("saved_fittings_json", null) ?: return
        try {
            val array = JSONArray(jsonString)
            val list = mutableListOf<TryOnResult>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val dressId = obj.getString("dressId")
                val dress = dresses.find { it.id == dressId } ?: dresses.first()
                val result = TryOnResult(
                    id = obj.getString("id"),
                    dressId = dressId,
                    dressTitleFa = obj.optString("dressTitleFa", dress.titleFa),
                    dressTitleEn = obj.optString("dressTitleEn", dress.titleEn),
                    modelId = obj.optString("modelId", null),
                    isCustomPhoto = obj.optBoolean("isCustomPhoto", false),
                    resultImagePath = obj.getString("resultImagePath"),
                    beforeImagePath = obj.optString("beforeImagePath", null),
                    beforeImageRes = R.drawable.model_bride_pose,
                    resultFallbackRes = when {
                        dress.category == DressCategory.MERMAID -> R.drawable.bridal_mermaid
                        dress.category == DressCategory.CORSET_LINGERIE -> R.drawable.lingerie_corset
                        else -> R.drawable.model_bride_fitted
                    },
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                    fitScore = obj.optInt("fitScore", 96),
                    notes = obj.optString("notes", ""),
                    config = FittingConfig(
                        dress = dress,
                        modelPose = modelPoses.firstOrNull { it.id == obj.optString("modelId") } ?: modelPoses.first()
                    )
                )
                list.add(result)
            }
            _savedLooks.value = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun persistSavedLooks(list: List<TryOnResult>) {
        try {
            val array = JSONArray()
            for (item in list) {
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("dressId", item.dressId)
                    put("dressTitleFa", item.dressTitleFa)
                    put("dressTitleEn", item.dressTitleEn)
                    put("modelId", item.modelId)
                    put("isCustomPhoto", item.isCustomPhoto)
                    put("resultImagePath", item.resultImagePath)
                    put("beforeImagePath", item.beforeImagePath)
                    put("timestamp", item.timestamp)
                    put("fitScore", item.fitScore)
                    put("notes", item.notes)
                }
                array.put(obj)
            }
            prefs.edit().putString("saved_fittings_json", array.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * AI Bridal Stylist Consultation Algorithm
     */
    fun getStylistAdvice(
        bodyShapeKey: String,
        venueKey: String,
        seasonKey: String
    ): StylistAdvice {
        return when (bodyShapeKey) {
            "hourglass" -> StylistAdvice(
                bodyShapeFa = "ساعت شنی (Hourglass)",
                bodyShapeEn = "Hourglass",
                bestSilhouettesFa = listOf(
                    "لباس عروس مدل ماهی (Mermaid) برای نمایش قوس‌های طبیعی کمر",
                    "کرست فنردوزی شده با یقه دلبری یا قلبی",
                    "مدل پرنسسی با کمربند ظریف نگین‌دار"
                ),
                veilRecommendationFa = "تور بلند چپل حاشیه‌دوزی شده (۲.۵ تا ۳ متر) یا تور سرشانه که جلوه کمر لباس را نپوشاند.",
                jewelryRecommendationFa = "سرویس تنیس الماس یا گردنبند چوکر مروارید با گوشواره‌های آویز ظریف.",
                fabricAdviceFa = "دانتل شاین فرانسه، کرپ کادی سنگین و ساتن دوشس براق جلوه بی‌نظیری به اندام شما می‌دهند.",
                generalTipFa = "اندام شما برای مدل ماهی سرنا و کرست‌های بانوت ایده‌آل است؛ از پف دادن بیش از حد در ناحیه باسن خودداری کنید تا تعادل فرم ساعت شنی حفظ شود."
            )
            "pear" -> StylistAdvice(
                bodyShapeFa = "گلابی (Pear Shape)",
                bodyShapeEn = "Pear",
                bestSilhouettesFa = listOf(
                    "لباس عروس پرنسسی باشکوه (Ballgown) که پهنای باسن را می‌پوشاند",
                    "مدل ای‌لاین (A-Line) با دامن ارگانزا",
                    "بالاتنه کار شده با سنگ و یقه قایقی یا آستین‌های افتاده برای پهن‌تر نشان دادن شانه‌ها"
                ),
                veilRecommendationFa = "تور متوسط فرانسوی یا تور سلطنتی تا انتهای دنباله که نگاه‌ها را به بالا هدایت کند.",
                jewelryRecommendationFa = "گوشواره‌های بزرگ بیانی (Statement) و گردنبند پرکار برای جلب توجه به چهره و گردن.",
                fabricAdviceFa = "بالاتنه با دانتل مرواریددوزی شده سنگین و دامن با تافته سبک یا تور فرانسوی.",
                generalTipFa = "لباس عروس پرنسسی الیزابت بهترین انتخاب برای شماست تا تعادلی رؤیایی بین شانه و دامن ایجاد شود."
            )
            "rectangle" -> StylistAdvice(
                bodyShapeFa = "مستطیلی / ورزشکاری (Rectangle)",
                bodyShapeEn = "Rectangle",
                bestSilhouettesFa = listOf(
                    "لباس کرست‌دار با فنربندی سفت برای ایجاد خط کمر باریک",
                    "مدل‌های با دامن چندلایه و چین‌دار برای ایجاد حجم در پایین‌تنه",
                    "یقه V باز و چاک‌های جذاب برای کشیده‌تر نشان دادن قامت"
                ),
                veilRecommendationFa = "تور لایه‌لایه پف‌دار که بعد و حجم بصری به استایل اضافه کند.",
                jewelryRecommendationFa = "گردنبند V شکل کشیده با گوشواره‌های میخی الماس و تاج پرنسسی بلند.",
                fabricAdviceFa = "ترکیب ارگانزا با گل‌های سه‌بعدی و حریر دامن موج‌دار.",
                generalTipFa = "کرست‌های اختصاصی بانوت با فنربندی ارگونومیک به اندام شما انحنای زنانه فوق‌العاده‌ای می‌بخشند."
            )
            else -> StylistAdvice(
                bodyShapeFa = "سیبی / متناسب (Apple & Petite)",
                bodyShapeEn = "Apple / Petite",
                bestSilhouettesFa = listOf(
                    "مدل ای‌لاین رمانتیک با خط کمر کمی بالاتر (امپایر)",
                    "یقه هفت (V-Neck) باز و آستین‌های تور حریر ظریف",
                    "پارچه‌های لخت و روان بدون چین‌های حجیم در ناحیه شکم"
                ),
                veilRecommendationFa = "تور بلند ساده ابریشمی که خط عمودی ممتد بسازد و شما را قدبلندتر نشان دهد.",
                jewelryRecommendationFa = "گوشواره‌های کشیده و نیم‌تاج ظریف کریستال.",
                fabricAdviceFa = "حریر ژرژت، کرپ ابریشم و دانتل‌های ریزبافت دست‌دوز.",
                generalTipFa = "لباس ای‌لاین ژولیت کشیدگی قد و تناسب فرم سینه و کمر شما را به شکلی خیره‌کننده نشان خواهد داد."
            )
        }
    }
}
