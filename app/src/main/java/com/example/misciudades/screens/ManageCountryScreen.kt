package com.example.misciudades.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.misciudades.data.Capital
import com.example.misciudades.data.CapitalRepository
import com.example.misciudades.ui.theme.Purple40
import com.example.misciudades.ui.theme.PurpleGrey40
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCountryScreen(
    repository: CapitalRepository,
    onBack: () -> Unit
) {
    val vm: ManageCountryViewModel = viewModel(
        factory = ManageCountryViewModel.provideFactory(repository)
    )
    val countries by vm.countries.collectAsState()
    var countryToDelete by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Países") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple40,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(countries) { (pais, caps) ->
                    // Estilo idéntico al de las cards de ciudades
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(PurpleGrey40.copy(alpha = 0.05f))
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                pais,
                                style = MaterialTheme.typography.titleMedium,
                                color = Purple40
                            )
                            Spacer(Modifier.height(4.dp))
                            caps.forEach { cap ->
                                Text(
                                    "• ${cap.ciudad} (${cap.poblacion})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                                )
                            }
                        }
                        IconButton(onClick = { countryToDelete = pais }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Borrar país",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        if (countryToDelete != null) {
            AlertDialog(
                onDismissRequest = { countryToDelete = null },
                title = { Text("Confirmar eliminacion") },
                text = { Text("¿Desea borrar \"$countryToDelete\"? " +
                        "\nSe borraran sus ciudades") },
                confirmButton = {
                    TextButton(onClick = {
                        vm.deleteCountry(countryToDelete!!)
                        countryToDelete = null
                    }) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { countryToDelete = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

class ManageCountryViewModel(private val repo: CapitalRepository) : ViewModel() {
    private val _countries = MutableStateFlow<List<Pair<String, List<Capital>>>>(emptyList())
    val countries: StateFlow<List<Pair<String, List<Capital>>>> = _countries

    init { loadGroups() }
    private fun loadGroups() = viewModelScope.launch {
        _countries.value = repo.listarTodas().groupBy { it.pais }.toList()
    }

    fun deleteCountry(pais: String) = viewModelScope.launch {
        repo.borrarPorPais(pais)
        loadGroups()
    }

    companion object {
        fun provideFactory(repo: CapitalRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ManageCountryViewModel(repo) as T
                }
            }
    }
}
