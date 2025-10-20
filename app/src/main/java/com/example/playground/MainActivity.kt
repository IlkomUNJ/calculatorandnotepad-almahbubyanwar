package com.example.playground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.playground.ui.theme.PlaygroundTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlaygroundTheme {
                Scaffold( modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Main(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    data object Home: Screen("titlescreen")
    data object Calculator: Screen("calc")
    data object TextEdit: Screen("textedit")
}

@Composable
fun Main(modifier: Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(route = Screen.Home.route) {
            TitleScreen(modifier,
                onNavigateToCalc = {navController.navigate(Screen.Calculator.route)},
                onNavigateToTextEdit = {navController.navigate(Screen.TextEdit.route)}
                )
        }
        composable(route = Screen.Calculator.route) {
            CalcScreen(modifier, onNavigateBack = { navController.popBackStack() })
        }
        composable(route = Screen.TextEdit.route) {
            TextEditor(modifier, onNavigateBack = { navController.popBackStack() })
        }
    }
//    CalcScreen(modifier = modifier)
//    TextEditor(modifier = modifier)
}

@Composable
fun TitleScreen(modifier: Modifier = Modifier, onNavigateToCalc: () -> Unit,
                onNavigateToTextEdit: () -> Unit) {
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("hello there!", color = MaterialTheme.colorScheme.onBackground)
        Button(onClick = onNavigateToCalc) {
            Text("Calculator", color = MaterialTheme.colorScheme.onPrimary)
        }
        Button(onClick = onNavigateToTextEdit) {
            Text("Text Editor", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Preview
@Composable
fun ComposablePreview() {
    Main(Modifier)
}