package com.example.misciudades.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.misciudades.data.Capital
import com.example.misciudades.data.CapitalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel
import androidx.lifecycle.viewModelScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,          // ya no recibimos repository aquí
    onAdd: () -> Unit,
    onEdit: (Int) -> Unit,
    onSearch: () -> Unit,
    onManageCountry: () -> Unit
) {
    val lista by viewModel.lista.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Capitales") },
                actions = {
                    IconButton(onClick = onSearch) {
                        Icon(Icons.Filled.Search, contentDescription = "Buscar")
                    }
                    IconButton(onClick = onManageCountry) {
                        Icon(Icons.Filled.Delete, contentDescription = "Borrar por País")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            items(lista) { capital ->
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEdit(capital.id) },
                    headlineContent   = { Text(capital.ciudad) },
                    supportingContent = { Text(capital.pais) },
                    trailingContent   = {
                        IconButton(onClick = { viewModel.borrarCiudad(capital.ciudad) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Borrar")
                        }
                    }
                )
                Divider()
            }
        }
    }
}

class HomeViewModel(private val repository: CapitalRepository) : ViewModel() {
    private val _lista = MutableStateFlow<List<Capital>>(emptyList())
    val lista: StateFlow<List<Capital>> = _lista

    init { loadAll() }

    // Hacemos público loadAll()
    fun loadAll() {
        viewModelScope.launch {
            _lista.value = repository.listarTodas()
        }
    }

    fun borrarCiudad(ciudad: String) {
        viewModelScope.launch {
            repository.borrarPorCiudad(ciudad)
            loadAll()
        }
    }

    companion object {
        fun provideFactory(repo: CapitalRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(repo) as T
                }
            }
    }
}
