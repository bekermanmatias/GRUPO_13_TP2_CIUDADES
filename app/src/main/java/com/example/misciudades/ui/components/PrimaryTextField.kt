package com.example.misciudades.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Un OutlinedTextField estilizado con los colores del tema.
 *
 * @param value       El texto actual.
 * @param onValueChange  Lambda que recibe el nuevo valor.
 * @param label       Texto de la etiqueta.
 * @param leadingIcon Composable opcional para un icono al inicio.
 * @param modifier    Modifier para posicionarlo.
 */
@Composable
fun PrimaryTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor   = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
        focusedLabelColor    = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor  = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        cursorColor          = MaterialTheme.colorScheme.primary
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = leadingIcon,
        singleLine = true,
        colors = colors,
        modifier = modifier.fillMaxWidth()
    )
}
