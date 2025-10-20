package com.example.playground

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalcScreen(modifier: Modifier, calcViewModel: CalcViewModel = viewModel(),
               onNavigateBack: () -> Unit) {
    val uiState by calcViewModel.uiState.collectAsState()
    val currentMode = uiState.currentMode
    val currentCalculator = uiState.currentInput

    fun switchMode() {
        calcViewModel.onSwitchMode()
    }

    fun processInput(symbol: String) {
        calcViewModel.onUserInput(symbol)
    }

    Column() {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title={
            Text("Calculator")
        }, navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go Back"
                )
            }
        })
        NumDisplay(displayString = displayInfix(currentCalculator))
        if (currentMode == Mode.SCIENTIFIC) {
            ScientificView(currentMode, switchMode = { switchMode() }, processInput = ::processInput)
        }
        else {
            BasicView(currentMode, switchMode = {switchMode()}, processInput = ::processInput)
        }
    }
}

@Composable
fun BasicView(currentMode: Mode, switchMode: () -> Unit, modifier: Modifier = Modifier,
              processInput: (String) -> Unit) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WeightedSpacer()
            WeightedSpacer()
            WeightedSpacer()
            BackspaceButton(processInput = processInput)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WeightedFuncButton("C", processInput = processInput)
            WeightedFuncButton("(", processInput = processInput)
            WeightedFuncButton(")", processInput = processInput)
            WeightedFuncButton("/", processInput = processInput)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WeightedNumButton("7", processInput = processInput)
            WeightedNumButton("8", processInput = processInput)
            WeightedNumButton("9", processInput = processInput)
            WeightedFuncButton("×", processInput = processInput)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WeightedNumButton("4", processInput = processInput)
            WeightedNumButton("5", processInput = processInput)
            WeightedNumButton("6", processInput = processInput)
            WeightedFuncButton("-", processInput = processInput)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WeightedNumButton("1", processInput = processInput)
            WeightedNumButton("2", processInput = processInput)
            WeightedNumButton("3", processInput = processInput)
            WeightedFuncButton("+", processInput = processInput)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SwitchModeButton(currentMode, switchMode)
            WeightedNumButton("0", processInput = processInput)
            WeightedFuncButton(".", processInput = processInput)
            EqualsButton(processInput = processInput)
        }
    }
}

@Composable
fun ScientificView(currentMode: Mode, switchMode: () -> Unit, modifier: Modifier = Modifier,
                   processInput: (String) -> Unit) {
    val smallerFont = MaterialTheme.typography.headlineSmall.fontSize

    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WeightedFuncButton("√", processInput = processInput)
            WeightedFuncButton("sin", processInput = processInput, fontSize = smallerFont)
            WeightedFuncButton("cos", processInput = processInput, fontSize = smallerFont)
            WeightedFuncButton("tan", processInput = processInput, fontSize = smallerFont)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WeightedFuncButton("ln", processInput = processInput)
            WeightedFuncButton("arcsin", processInput = processInput, fontSize = smallerFont)
            WeightedFuncButton("arccos", processInput = processInput, fontSize = smallerFont)
            WeightedFuncButton("arctan", processInput = processInput, fontSize = smallerFont)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SwitchModeButton(currentMode, switchMode)
            WeightedFuncButton("1/x", processInput = processInput, fontSize = smallerFont)
            WeightedFuncButton("x!", processInput = processInput)
            WeightedFuncButton("^", processInput = processInput)
        }
    }
}

@Composable
fun RowScope.WeightedSpacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.weight(1f))
}

@Composable
fun RowScope.SwitchModeButton(currentMode: Mode, switchMode: () -> Unit, modifier: Modifier = Modifier) {
    FilledTonalButton(onClick = switchMode, modifier = modifier.weight(1f).aspectRatio(1f)) {
        Text(text = if (currentMode == Mode.BASIC) "BASIC" else "SCI.",
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun NumDisplay(modifier: Modifier = Modifier, displayString: String) {
    val scrollState = rememberScrollState()

    LaunchedEffect(displayString) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    Row(modifier = Modifier.fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 32.dp)
        .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = displayString,
            fontSize = MaterialTheme.typography.displayMedium.fontSize,
            textAlign = TextAlign.End,
            softWrap = false,
            maxLines = 1
        )
    }
}

@Composable
fun RowScope.EqualsButton(modifier: Modifier = Modifier, processInput: (String) -> Unit) {
    Button(onClick = { processInput("=") }, modifier = modifier.weight(1f).aspectRatio(1f)) {
        ButtonText("=", modifier = modifier, fontSize = MaterialTheme.typography.displaySmall.fontSize)
    }
}

@Composable
fun RowScope.BackspaceButton(modifier: Modifier = Modifier, processInput: (String) -> Unit) {
    FilledTonalButton(onClick = { processInput("BS") }, modifier = modifier.weight(1f).aspectRatio(1f)) {
        Icon(imageVector = Icons.AutoMirrored.Filled.Backspace,
            contentDescription = "Backspace"
        )
    }
}

@Composable
fun RowScope.WeightedNumButton(symbol: String, modifier: Modifier = Modifier,
                               fontSize: TextUnit = MaterialTheme.typography.displaySmall.fontSize,
                               processInput: (String) -> Unit
) {
    FilledTonalButton(onClick = { processInput(symbol) }, modifier = modifier.aspectRatio(1f).weight(1f), shape = CircleShape) {
        ButtonText(symbol, modifier, fontSize)
    }
}

@Composable
fun RowScope.WeightedFuncButton(symbol: String, modifier: Modifier = Modifier,
                                fontSize: TextUnit = MaterialTheme.typography.displaySmall.fontSize,
                                processInput: (String) -> Unit
) {
    FilledTonalButton(onClick = { processInput(symbol) }, modifier = modifier.aspectRatio(1f).weight(1f), shape = CircleShape) {
        ButtonText(symbol, modifier, fontSize)
    }
}

@Composable
fun ButtonText(innerText : String, modifier: Modifier = Modifier,
               fontSize: TextUnit) {
    Text(innerText, fontSize = fontSize)
}