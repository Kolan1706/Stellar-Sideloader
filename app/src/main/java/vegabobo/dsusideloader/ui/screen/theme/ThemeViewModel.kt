package vegabobo.dsusideloader.ui.screen.theme

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vegabobo.dsusideloader.core.BaseViewModel
import vegabobo.dsusideloader.preferences.AppPrefs
import vegabobo.dsusideloader.ui.theme.stellar.StellarAccent
import vegabobo.dsusideloader.ui.theme.stellar.ThemePreset

data class ThemeSettings(
    val selectedAccent: StellarAccent = StellarAccent.TEAL,
    val selectedPreset: ThemePreset = ThemePreset.SPACE_CUSTOM,
    val blurValue: Float = 0.3f,
    val backgroundUri: String = "",
)

@HiltViewModel
class ThemeViewModel @Inject constructor(
    override val dataStore: DataStore<Preferences>,
) : BaseViewModel(dataStore) {

    private val _uiState = MutableStateFlow(ThemeSettings())
    val uiState: StateFlow<ThemeSettings> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val accentStr = readStringPref(AppPrefs.THEME_ACCENT)
            val presetStr = readStringPref(AppPrefs.THEME_PRESET)
            val blur = readStringPref(AppPrefs.THEME_BLUR).toFloatOrNull() ?: 0.3f
            val bgUri = readStringPref(AppPrefs.THEME_BACKGROUND_URI)

            _uiState.value = ThemeSettings(
                selectedAccent = StellarAccent.values().firstOrNull { it.name == accentStr } ?: StellarAccent.TEAL,
                selectedPreset = ThemePreset.values().firstOrNull { it.name == presetStr } ?: ThemePreset.SPACE_CUSTOM,
                blurValue = blur,
                backgroundUri = bgUri,
            )
        }
    }

    fun updateAccent(accent: StellarAccent) {
        _uiState.value = _uiState.value.copy(selectedAccent = accent)
        viewModelScope.launch {
            updateStringPref(AppPrefs.THEME_ACCENT, accent.name)
        }
    }

    fun updatePreset(preset: ThemePreset) {
        _uiState.value = _uiState.value.copy(selectedPreset = preset)
        viewModelScope.launch {
            updateStringPref(AppPrefs.THEME_PRESET, preset.name)
        }
    }

    fun updateBlur(blur: Float) {
        _uiState.value = _uiState.value.copy(blurValue = blur)
        viewModelScope.launch {
            updateStringPref(AppPrefs.THEME_BLUR, blur.toString())
        }
    }

    fun updateBackgroundUri(uri: String) {
        _uiState.value = _uiState.value.copy(backgroundUri = uri)
        viewModelScope.launch {
            updateStringPref(AppPrefs.THEME_BACKGROUND_URI, uri)
        }
    }
}
