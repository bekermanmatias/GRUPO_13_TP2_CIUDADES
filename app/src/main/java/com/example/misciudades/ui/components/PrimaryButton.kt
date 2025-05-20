package com.example.misciudades.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.misciudades.ui.theme.Purple40

/**
 * Un Button estilizado con el color primario de tu tema.
 *
 * @param text     Texto que mostrará.
 * @param onClick  Lambda que maneja el clic.
 * @param modifier Modifier para posicionarlo.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Purple40,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier
    ) {
        Text(text)
    }
}
