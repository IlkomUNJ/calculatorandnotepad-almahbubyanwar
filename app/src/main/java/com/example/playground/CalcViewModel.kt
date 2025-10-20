package com.example.playground

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class CalcState(
    val currentInput: List<String> = listOf<String>(),
    val currentMode: Mode = Mode.BASIC
)

class CalcViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CalcState())
    val uiState: StateFlow<CalcState> = _uiState.asStateFlow()

    fun onSwitchMode() {
        _uiState.update {
            currentState -> currentState.copy(
                currentMode = if (currentState.currentMode == Mode.BASIC) {
                    Mode.SCIENTIFIC
                } else {
                    Mode.BASIC
                }
            )
        }
    }

    fun onUserInput(symbol: String) {
        val prevInput = _uiState.value.currentInput

        _uiState.update { currentState -> currentState.copy(
            currentInput = parseInfix(prevInput, symbol)
        )
        }
    }
}