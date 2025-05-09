package com.example.misciudades.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.misciudades.data.CapitalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun EditCapitalScreen(
    repository: CapitalRepository,
    mode: String,
    capitalId: Int,
    onSaved: () -> Unit
) {
    var pais by rememberSaveable { mutableStateOf("") }
    var ciudad by rememberSaveable { mutableStateOf("") }
    var poblacionStr by rememberSaveable { mutableStateOf("") }

    // Cargar solo en modo "edit"
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

    Column(Modifier.padding(16.dp)) {
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
                    if (mode == "add") {
                        repository.insertar(pais, ciudad, p)
                    } else {
                        repository.actualizarCapital(capitalId, pais, ciudad, p)
                    }
                    onSaved()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(if (mode == "add") "Guardar" else "Actualizar")
        }
    }
}
