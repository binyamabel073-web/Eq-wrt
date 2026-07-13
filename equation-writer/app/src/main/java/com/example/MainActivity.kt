package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize Database, Dao and Repository
    val database = EquationDatabase.getDatabase(this)
    val dao = database.equationDao()
    val repository = EquationRepository(dao)
    val viewModelFactory = EquationViewModelFactory(repository)

    setContent {
      MyApplicationTheme {
        val viewModel: EquationViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
          factory = viewModelFactory
        )
        EquationEditorScreen(
          viewModel = viewModel,
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}

