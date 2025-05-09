package com.example.misciudades.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.misciudades.data.CapitalRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun EditCapitalScreen(
    repository: CapitalRepository,
    mode: String,
    capitalId: Int,
    onSaved: () -> Unit
) {
    val factory = EditCapitalViewModel.provideFactory(repository)
    val viewModel: EditCapitalViewModel = composeViewModel(factory = factory)
    var pais by rememberSaveable { mutableStateOf("") }
    var ciudad by rememberSaveable { mutableStateOf("") }
    var poblacionStr by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(capitalId, mode) {
        if (mode == "edit" && capitalId != -1) {
            val cap = withContext(Dispatchers.IO) { repository.consultarPorId(capitalId) }
            cap?.let {
                pais = it.pais
                ciudad = it.ciudad
                poblacionStr = it.poblacion.toString()
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = pais,
            onValueChange = { pais = it },
            label = { Text("País") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = ciudad,
            onValueChange = { ciudad = it },
            label = { Text("Ciudad") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = poblacionStr,
            onValueChange = { poblacionStr = it },
            label = { Text("Población") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(
            onClick = {
                poblacionStr.toIntOrNull()?.let { p ->
                    if (mode == "add") viewModel.insertar(pais, ciudad, p)
                    else viewModel.actualizarPoblacion(ciudad, p)
                    onSaved()
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(if (mode == "add") "Guardar" else "Actualizar")
        }
    }
}

class EditCapitalViewModel(private val repository: CapitalRepository) : ViewModel() {
    fun insertar(pais: String, ciudad: String, poblacion: Int) {
        viewModelScope.launch {
            repository.insertar(pais, ciudad, poblacion)
        }
    }

    fun actualizarPoblacion(ciudad: String, poblacion: Int) {
        viewModelScope.launch {
            repository.actualizarPoblacion(ciudad, poblacion)
        }
    }

    companion object {
        fun provideFactory(repo: CapitalRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return EditCapitalViewModel(repo) as T
                }
            }
    }
}
