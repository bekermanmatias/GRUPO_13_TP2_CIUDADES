package com.example.misciudades.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.misciudades.data.CapitalRepository
import com.example.misciudades.ui.theme.Purple40
import com.example.misciudades.ui.theme.PurpleGrey40
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCapitalScreen(
    repository: CapitalRepository,
    mode: String,
    capitalId: Int,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    var pais by rememberSaveable { mutableStateOf("") }
    var ciudad by rememberSaveable { mutableStateOf("") }
    var poblacionStr by rememberSaveable { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    var paisError by remember { mutableStateOf(false) }
    var ciudadError by remember { mutableStateOf(false) }
    var poblacionError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(mode, capitalId) {
        if (mode == "edit" && capitalId != -1) {
            val cap = withContext(Dispatchers.IO) { repository.consultarPorId(capitalId) }
            cap?.let {
                pais = it.pais
                ciudad = it.ciudad
                poblacionStr = it.poblacion.toString()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (mode == "add") "Agregar Capital" else "Editar Capital") },
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = pais,
                    onValueChange = {
                        pais = it
                        paisError = false
                        showError = false
                    },
                    placeholder = { Text("País") },
                    isError = paisError,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (paisError) Color.Red else Purple40,
                        unfocusedBorderColor = if (paisError) Color.Red else PurpleGrey40,
                        focusedContainerColor = PurpleGrey40.copy(alpha = 0.1f),
                        unfocusedContainerColor = PurpleGrey40.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = ciudad,
                    onValueChange = {
                        ciudad = it
                        ciudadError = false
                        showError = false
                    },
                    placeholder = { Text("Ciudad") },
                    isError = ciudadError,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (ciudadError) Color.Red else Purple40,
                        unfocusedBorderColor = if (ciudadError) Color.Red else PurpleGrey40,
                        focusedContainerColor = PurpleGrey40.copy(alpha = 0.1f),
                        unfocusedContainerColor = PurpleGrey40.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = poblacionStr,
                    onValueChange = {
                        poblacionStr = it
                        poblacionError = false
                        showError = false
                    },
                    placeholder = { Text("Población") },
                    isError = poblacionError,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (poblacionError) Color.Red else Purple40,
                        unfocusedBorderColor = if (poblacionError) Color.Red else PurpleGrey40,
                        focusedContainerColor = PurpleGrey40.copy(alpha = 0.1f),
                        unfocusedContainerColor = PurpleGrey40.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (showError) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp).fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        scope.launch {
                            val paisValido = pais.trim().length >= 3
                            val ciudadValida = ciudad.trim().length >= 3
                            val poblacion = poblacionStr.toIntOrNull()

                            paisError = !paisValido
                            ciudadError = !ciudadValida
                            poblacionError = poblacion == null

                            if (!paisValido) {
                                errorMessage = "El país debe tener al menos 3 caracteres"
                                showError = true
                                return@launch
                            }
                            if (!ciudadValida) {
                                errorMessage = "La ciudad debe tener al menos 3 caracteres"
                                showError = true
                                return@launch
                            }
                            if (poblacion == null) {
                                errorMessage = "La población debe ser un número válido"
                                showError = true
                                return@launch
                            }

                            val ciudadExistente = withContext(Dispatchers.IO) {
                                repository.consultarPorNombre(ciudad.trim())
                            }
                            if (ciudadExistente != null && (mode == "add" || ciudadExistente.id != capitalId)) {
                                errorMessage = "Ya existe una capital con ese nombre"
                                showError = true
                                return@launch
                            }

                            showError = false
                            withContext(Dispatchers.IO) {
                                if (mode == "add") {
                                    repository.insertar(pais.trim(), ciudad.trim(), poblacion)
                                } else {
                                    repository.actualizarCapital(capitalId, pais.trim(), ciudad.trim(), poblacion)
                                }
                            }
                            onSaved()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple40,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(if (mode == "add") "Guardar" else "Actualizar")
                }
            }
        }
    }
}

