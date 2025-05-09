package com.example.misciudades.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.misciudades.data.CapitalRepository
import kotlinx.coroutines.launch

@Composable
fun ManageCountryScreen(
    repository: CapitalRepository,
    onBack: () -> Unit
) {
    // ViewModel para gestionar país
    val factory = ManageCountryViewModel.provideFactory(repository)
    val viewModel: ManageCountryViewModel = composeViewModel(factory = factory)

    var pais by rememberSaveable { mutableStateOf("") }
    var showConfirm by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = pais,
            onValueChange = { pais = it },
            label = { Text("Nombre del país") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { showConfirm = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Borrar todas las capitales de $pais")
        }

        if (showConfirm) {
            AlertDialog(
                onDismissRequest = { showConfirm = false },
                title = { Text("Confirmar borrado") },
                text = { Text("¿Estás seguro que deseas borrar todas las capitales de $pais?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.borrarPorPais(pais)
                        showConfirm = false
                        onBack()
                    }) {
                        Text("Sí, borrar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirm = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        Spacer(Modifier.weight(1f))
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Volver")
        }
    }
}

class ManageCountryViewModel(private val repo: CapitalRepository) : ViewModel() {
    fun borrarPorPais(pais: String) {
        viewModelScope.launch {
            repo.borrarPorPais(pais)
        }
    }

    companion object {
        fun provideFactory(repo: CapitalRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ManageCountryViewModel(repo) as T
                }
            }
    }
}