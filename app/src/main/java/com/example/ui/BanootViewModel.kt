package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BanootRepository
import com.example.engine.BanootCoreEngine
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppScreen {
    TRY_ON,
    CATALOG,
    AI_STYLIST,
    LOOKBOOK
}

data class TryOnUiState(
    val selectedDress: BridalDress,
    val selectedModel: ModelPose,
    val customPhotoUri: Uri? = null,
    val isCustomPhotoActive: Boolean = false,
    val waistAdjustment: Int = 0,
    val bustAdjustment: Int = 0,
    val veilStyle: VeilStyle = VeilStyle.CHAPEL_LACE,
    val tiaraStyle: TiaraStyle = TiaraStyle.ROYAL_CROWN,
    val lightingPreset: LightingPreset = LightingPreset.GRAND_BALLROOM,
    val isProcessing: Boolean = false,
    val progressPercent: Float = 0f,
    val currentStepText: String = "",
    val latestResult: TryOnResult? = null,
    val showResultSheet: Boolean = false,
    val sliderPosition: Float = 0.5f,
    val userNotes: String = ""
)

class BanootViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BanootRepository(application)
    private val coreEngine = BanootCoreEngine(application)

    // UI Navigation
    private val _currentScreen = MutableStateFlow(AppScreen.TRY_ON)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Language toggle: true = Persian (Fa), false = English (En)
    private val _isFarsi = MutableStateFlow(true)
    val isFarsi: StateFlow<Boolean> = _isFarsi.asStateFlow()

    // Data lists
    val dresses = repository.dresses
    val modelPoses = repository.modelPoses
    val savedLooks = repository.savedLooks

    // Virtual Try-On State
    private val _tryOnState = MutableStateFlow(
        TryOnUiState(
            selectedDress = repository.dresses.first(),
            selectedModel = repository.modelPoses.first()
        )
    )
    val tryOnState: StateFlow<TryOnUiState> = _tryOnState.asStateFlow()

    // Catalog inspection
    private val _inspectedDress = MutableStateFlow<BridalDress?>(null)
    val inspectedDress: StateFlow<BridalDress?> = _inspectedDress.asStateFlow()

    // AI Stylist
    private val _stylistBodyShape = MutableStateFlow("hourglass")
    val stylistBodyShape: StateFlow<String> = _stylistBodyShape.asStateFlow()

    private val _stylistVenue = MutableStateFlow("ballroom")
    val stylistVenue: StateFlow<String> = _stylistVenue.asStateFlow()

    private val _stylistSeason = MutableStateFlow("spring_summer")
    val stylistSeason: StateFlow<String> = _stylistSeason.asStateFlow()

    private val _stylistAdvice = MutableStateFlow<StylistAdvice?>(
        repository.getStylistAdvice("hourglass", "ballroom", "spring_summer")
    )
    val stylistAdvice: StateFlow<StylistAdvice?> = _stylistAdvice.asStateFlow()

    // Side by Side Comparison
    private val _comparisonA = MutableStateFlow<TryOnResult?>(null)
    val comparisonA: StateFlow<TryOnResult?> = _comparisonA.asStateFlow()

    private val _comparisonB = MutableStateFlow<TryOnResult?>(null)
    val comparisonB: StateFlow<TryOnResult?> = _comparisonB.asStateFlow()

    private val _isComparing = MutableStateFlow(false)
    val isComparing: StateFlow<Boolean> = _isComparing.asStateFlow()

    // Notification toast
    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun toggleLanguage() {
        _isFarsi.value = !_isFarsi.value
    }

    fun selectDress(dress: BridalDress) {
        _tryOnState.value = _tryOnState.value.copy(selectedDress = dress)
    }

    fun selectModel(model: ModelPose) {
        _tryOnState.value = _tryOnState.value.copy(
            selectedModel = model,
            isCustomPhotoActive = false
        )
    }

    fun setCustomPhotoUri(uri: Uri?) {
        if (uri != null) {
            _tryOnState.value = _tryOnState.value.copy(
                customPhotoUri = uri,
                isCustomPhotoActive = true
            )
            showMessage(if (_isFarsi.value) "تصویر شما برای پرو بارگذاری شد" else "User photo uploaded for try-on")
        }
    }

    fun setUsePresetModel() {
        _tryOnState.value = _tryOnState.value.copy(isCustomPhotoActive = false)
    }

    fun updateWaistAdjustment(adjustment: Int) {
        _tryOnState.value = _tryOnState.value.copy(waistAdjustment = adjustment.coerceIn(-10, 10))
    }

    fun updateBustAdjustment(adjustment: Int) {
        _tryOnState.value = _tryOnState.value.copy(bustAdjustment = adjustment.coerceIn(-10, 10))
    }

    fun updateVeilStyle(veil: VeilStyle) {
        _tryOnState.value = _tryOnState.value.copy(veilStyle = veil)
    }

    fun updateTiaraStyle(tiara: TiaraStyle) {
        _tryOnState.value = _tryOnState.value.copy(tiaraStyle = tiara)
    }

    fun updateLightingPreset(preset: LightingPreset) {
        _tryOnState.value = _tryOnState.value.copy(lightingPreset = preset)
    }

    fun updateSliderPosition(pos: Float) {
        _tryOnState.value = _tryOnState.value.copy(sliderPosition = pos.coerceIn(0f, 1f))
    }

    fun updateNotes(notes: String) {
        _tryOnState.value = _tryOnState.value.copy(userNotes = notes)
    }

    fun openDressDetail(dress: BridalDress) {
        _inspectedDress.value = dress
    }

    fun closeDressDetail() {
        _inspectedDress.value = null
    }

    fun tryOnFromCatalog(dress: BridalDress) {
        selectDress(dress)
        _inspectedDress.value = null
        _currentScreen.value = AppScreen.TRY_ON
    }

    fun dismissResultSheet() {
        _tryOnState.value = _tryOnState.value.copy(showResultSheet = false)
    }

    fun startVirtualTryOn() {
        val state = _tryOnState.value
        if (state.isProcessing) return

        viewModelScope.launch {
            _tryOnState.value = state.copy(
                isProcessing = true,
                progressPercent = 0.05f,
                currentStepText = if (_isFarsi.value) "آغاز فرایند پرو هوشمند بانوت..." else "Starting Banoot AI Try-On..."
            )

            val config = FittingConfig(
                dress = state.selectedDress,
                modelPose = if (!state.isCustomPhotoActive) state.selectedModel else null,
                customUserPhotoUri = if (state.isCustomPhotoActive) state.customPhotoUri?.toString() else null,
                waistAdjustmentPercent = state.waistAdjustment,
                bustAdjustmentPercent = state.bustAdjustment,
                veilStyle = state.veilStyle,
                tiaraStyle = state.tiaraStyle,
                lightingPreset = state.lightingPreset
            )

            try {
                val result = coreEngine.executeBridalTryOn(config) { stepName, percent ->
                    _tryOnState.value = _tryOnState.value.copy(
                        currentStepText = stepName,
                        progressPercent = percent
                    )
                }

                _tryOnState.value = _tryOnState.value.copy(
                    isProcessing = false,
                    latestResult = result,
                    showResultSheet = true,
                    sliderPosition = 0.5f
                )

                // Auto save look to repository lookbook
                repository.saveLook(result)
                showMessage(if (_isFarsi.value) "پرو لباس عروس با موفقیت انجام شد!" else "Bridal Try-on completed successfully!")
            } catch (e: Exception) {
                _tryOnState.value = _tryOnState.value.copy(
                    isProcessing = false,
                    currentStepText = "خطا در پردازش"
                )
                showMessage(if (_isFarsi.value) "خطا در پردازش پرو. مجدداً تلاش کنید." else "Try-on error. Please retry.")
            }
        }
    }

    fun saveCurrentLookWithNotes(notes: String) {
        val result = _tryOnState.value.latestResult ?: return
        val updated = result.copy(notes = notes)
        repository.saveLook(updated)
        showMessage(if (_isFarsi.value) "در دفترچه پروهای شما ثبت شد" else "Saved to My Looks")
    }

    fun deleteSavedLook(id: String) {
        repository.deleteLook(id)
        if (_comparisonA.value?.id == id) _comparisonA.value = null
        if (_comparisonB.value?.id == id) _comparisonB.value = null
        showMessage(if (_isFarsi.value) "پرو حذف شد" else "Look removed")
    }

    // Comparison helpers
    fun startComparing(lookA: TryOnResult, lookB: TryOnResult) {
        _comparisonA.value = lookA
        _comparisonB.value = lookB
        _isComparing.value = true
    }

    fun stopComparing() {
        _isComparing.value = false
    }

    // AI Stylist updates
    fun updateStylistQuery(bodyShape: String, venue: String, season: String) {
        _stylistBodyShape.value = bodyShape
        _stylistVenue.value = venue
        _stylistSeason.value = season
        _stylistAdvice.value = repository.getStylistAdvice(bodyShape, venue, season)
    }

    fun showMessage(msg: String) {
        _feedbackMessage.value = msg
    }

    fun clearMessage() {
        _feedbackMessage.value = null
    }
}
