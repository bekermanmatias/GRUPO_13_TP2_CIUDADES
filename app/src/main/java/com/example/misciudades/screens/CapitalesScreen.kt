package com.example.misciudades.screens

import Capital
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CapitalesScreen(
    capitals: List<Capital>,
    onAgregar: (pais: String, ciudad: String, poblacion: Int) -> Unit,
    onBorrarCiudad: (ciudad: String) -> Unit,
    onBorrarPais: (pais: String) -> Unit,
    onActualizarPoblacion: (ciudad: String, poblacion: Int) -> Unit
) {
    var pais by rememberSaveable { mutableStateOf("") }
    var ciudad by rememberSaveable { mutableStateOf("") }
    var poblacionStr by rememberSaveable { mutableStateOf("") }

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
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Button(
            onClick = {
                poblacionStr.toIntOrNull()?.let {
                    onAgregar(pais, ciudad, it)
                    pais = ""; ciudad = ""; poblacionStr = ""
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Agregar Capital")
        }

        Spacer(Modifier.height(16.dp))

        Text("Capitales guardadas:", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(capitals) { cap ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${cap.pais} – ${cap.ciudad} (${cap.poblacion})")
                    IconButton(onClick = { onBorrarCiudad(cap.ciudad) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Borrar")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CapitalesScreenPreview() {
    val muestra = listOf(
        Capital(id = 1, pais = "España", ciudad = "Madrid", poblacion = 3223000),
        Capital(id = 2, pais = "Francia", ciudad = "París", poblacion = 2148000)
    )
    CapitalesScreen(
        capitals = muestra,
        onAgregar = { _, _, _ -> },
        onBorrarCiudad = {},
        onBorrarPais = {},
        onActualizarPoblacion = { _, _ -> }
    )
}
