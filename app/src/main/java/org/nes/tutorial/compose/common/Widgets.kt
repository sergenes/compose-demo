package org.nes.tutorial.compose.common

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NiceButton(
    title: String,
    textColor: Color = primaryTextColor,
    backgroundColor: Color = primaryColor,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .height(44.dp),
        shape = RoundedCornerShape(30),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = textColor,
            backgroundColor = backgroundColor
        )
    ) {
        Text(title)
    }
}
