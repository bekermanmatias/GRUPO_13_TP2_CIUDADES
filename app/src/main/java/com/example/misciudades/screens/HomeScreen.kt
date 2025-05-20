package com.example.misciudades.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.misciudades.data.Capital
import com.example.misciudades.data.CapitalRepository
import com.example.misciudades.ui.theme.Purple40
import com.example.misciudades.ui.theme.PurpleGrey40
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAdd: () -> Unit,
    onEdit: (Int) -> Unit,
    onManageCountry: () -> Unit
) {
    val lista by viewModel.lista.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var ciudadAEliminar by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isBlank()) viewModel.loadAll()
        else viewModel.searchCity(searchQuery)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Capitales") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple40,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = onManageCountry) {
                        Icon(Icons.Filled.Flag, contentDescription = "Gestionar Países")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAdd,
                containerColor = Purple40,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar ciudad")
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar ciudad") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple40,
                    unfocusedBorderColor = PurpleGrey40,
                    focusedContainerColor = PurpleGrey40.copy(alpha = 0.1f),
                    unfocusedContainerColor = PurpleGrey40.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Lista de tarjetas
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(lista) { capital ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(PurpleGrey40.copy(alpha = 0.05f))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                capital.ciudad,
                                style = MaterialTheme.typography.titleMedium,
                                color = Purple40
                            )
                            Text(
                                capital.pais,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Población: ${capital.poblacion}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        IconButton(onClick = { onEdit(capital.id) }) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Editar",
                                tint = Purple40
                            )
                        }
                        IconButton(onClick = { ciudadAEliminar = capital.ciudad }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Borrar",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            ciudadAEliminar?.let { ciudad ->
                AlertDialog(
                    onDismissRequest = { ciudadAEliminar = null },
                    title = { Text("Confirmar eliminación") },
                    text = { Text("¿Estás seguro que querés eliminar la ciudad \"$ciudad\"?") },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.borrarCiudad(ciudad)
                            ciudadAEliminar = null
                        }) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { ciudadAEliminar = null }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

// ViewModel sin cambios
class HomeViewModel(private val repository: CapitalRepository) : ViewModel() {
    private val _lista = MutableStateFlow<List<Capital>>(emptyList())
    val lista: StateFlow<List<Capital>> = _lista

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _lista.value = repository.listarTodas()
        }
    }

    fun searchCity(ciudad: String) {
        viewModelScope.launch {
            _lista.value = repository.buscarPorPrefijo(ciudad)
        }
    }

    fun borrarCiudad(ciudad: String) {
        viewModelScope.launch {
            repository.borrarPorCiudad(ciudad)
            loadAll()
        }
    }
}
