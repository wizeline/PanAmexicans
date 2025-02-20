package com.wizeline.panamexicans.presentation.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import com.wizeline.panamexicans.presentation.theme.DarkBlue
import com.wizeline.panamexicans.presentation.theme.Orange

@Composable
fun PrimaryColorButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(size = 4.dp),
        onClick = { onClick() },
        contentPadding = PaddingValues(vertical = 11.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Orange
        )
    ) {
        Text(
            text = text,
            color = White,
        )
    }
}