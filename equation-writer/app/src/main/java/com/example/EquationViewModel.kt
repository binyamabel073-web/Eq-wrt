package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EquationViewModel(private val repository: EquationRepository) : ViewModel() {

    // Saved equations state
    val savedEquations: StateFlow<List<Equation>> = repository.allEquations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current editing equation text (Unicode)
    private val _editorText = MutableStateFlow("")
    val editorText: StateFlow<String> = _editorText.asStateFlow()

    // Current editing LaTeX equivalent
    private val _latexText = MutableStateFlow("")
    val latexText: StateFlow<String> = _latexText.asStateFlow()

    // Active typing math style (defaults to Math Italic for variable writing)
    private val _activeStyle = MutableStateFlow(MathStyle.MATH_ITALIC)
    val activeStyle: StateFlow<MathStyle> = _activeStyle.asStateFlow()

    // Display rendering preferences
    private val _bgThemeName = MutableStateFlow("Elegant Dark")
    val bgThemeName: StateFlow<String> = _bgThemeName.asStateFlow()

    private val _isSerifFont = MutableStateFlow(true)
    val isSerifFont: StateFlow<Boolean> = _isSerifFont.asStateFlow()

    private val _fontSizeScale = MutableStateFlow(1.0f)
    val fontSizeScale: StateFlow<Float> = _fontSizeScale.asStateFlow()

    // Currently selected saved equation (for editing/updating)
    private val _currentEditingId = MutableStateFlow<Int?>(null)
    val currentEditingId: StateFlow<Int?> = _currentEditingId.asStateFlow()

    fun updateEditorText(text: String) {
        _editorText.value = text
    }

    fun updateLatexText(text: String) {
        _latexText.value = text
        _editorText.value = LatexToUnicodeConverter.convert(text)
    }

    fun setStyle(style: MathStyle) {
        _activeStyle.value = style
    }

    fun setTheme(theme: String) {
        _bgThemeName.value = theme
    }

    fun toggleFontSerif() {
        _isSerifFont.value = !_isSerifFont.value
    }

    fun setFontSizeScale(scale: Float) {
        _fontSizeScale.value = scale
    }

    fun loadPreset(preset: EquationPreset) {
        _editorText.value = preset.unicode
        _latexText.value = preset.latex
        _currentEditingId.value = null
    }

    fun loadEquation(equation: Equation) {
        _editorText.value = equation.unicodeContent
        _latexText.value = equation.latexContent
        _bgThemeName.value = equation.bgThemeName
        _isSerifFont.value = equation.isSerifFont
        _fontSizeScale.value = equation.fontSizeScale
        _currentEditingId.value = equation.id
    }

    fun clearEditor() {
        _editorText.value = ""
        _latexText.value = ""
        _currentEditingId.value = null
    }

    fun saveCurrentEquation(title: String, category: String) {
        val currentTitle = title.ifBlank { "Untitled Equation" }
        val currentCategory = category.ifBlank { "General" }
        
        viewModelScope.launch {
            val equation = Equation(
                id = _currentEditingId.value ?: 0,
                title = currentTitle,
                unicodeContent = _editorText.value,
                latexContent = _latexText.value,
                category = currentCategory,
                timestamp = System.currentTimeMillis(),
                bgThemeName = _bgThemeName.value,
                isSerifFont = _isSerifFont.value,
                fontSizeScale = _fontSizeScale.value
            )
            repository.insert(equation)
            
            // If it was a new save, we can fetch its ID or reset, let's keep it clean
            _currentEditingId.value = null
        }
    }

    fun deleteEquation(equation: Equation) {
        viewModelScope.launch {
            repository.delete(equation)
            if (_currentEditingId.value == equation.id) {
                clearEditor()
            }
        }
    }
}

class EquationViewModelFactory(private val repository: EquationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EquationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EquationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
