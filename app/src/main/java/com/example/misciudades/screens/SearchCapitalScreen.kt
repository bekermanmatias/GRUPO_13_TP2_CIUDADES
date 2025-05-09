package com.example.misciudades.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.misciudades.data.Capital
import com.example.misciudades.data.CapitalRepository
import kotlinx.coroutines.launch

@Composable
fun SearchCapitalScreen(
    repository: CapitalRepository,
    onBack: () -> Unit
) {
    // Obtengo el ViewModel con el repo
    val factory = SearchCapitalViewModel.provideFactory(repository)
    val viewModel: SearchCapitalViewModel = composeViewModel(factory = factory)

    var query by rememberSaveable { mutableStateOf("") }
    var resultado by remember { mutableStateOf<Capital?>(null) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Ciudad") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.buscarPorCiudad(query) { resultado = it }
            },
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        ) {
            Text("Buscar")
        }

        Spacer(Modifier.height(16.dp))

        // Dos casos: encontrada o no encontrada
        if (resultado != null) {
            // Capital encontrada
            val cap = resultado!!
            Text("Encontrada: ${cap.ciudad}, ${cap.pais} (${cap.poblacion})")
            Row(modifier = Modifier.padding(top = 8.dp)) {
                Button(onClick = {
                    viewModel.borrarCiudad(cap.ciudad)
                    onBack()
                }) {
                    Text("Borrar")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    viewModel.actualizarPoblacion(cap.ciudad, cap.poblacion + 1)
                    onBack()
                }) {
                    Text("+1 Pobla")
                }
            }
        } else if (query.isNotBlank()) {
            // Búsqueda sin resultados
            Text("No encontrada", modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}

class SearchCapitalViewModel(private val repo: CapitalRepository) : ViewModel() {
    fun buscarPorCiudad(nombre: String, callback: (Capital?) -> Unit) {
        viewModelScope.launch {
            callback(repo.consultarPorCiudad(nombre))
        }
    }

    fun borrarCiudad(nombre: String) {
        viewModelScope.launch {
            repo.borrarPorCiudad(nombre)
        }
    }

    fun actualizarPoblacion(ciudad: String, poblacion: Int) {
        viewModelScope.launch {
            repo.actualizarPoblacion(ciudad, poblacion)
        }
    }

    companion object {
        fun provideFactory(
            repo: CapitalRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    SearchCapitalViewModel(repo) as T
            }
    }
}
