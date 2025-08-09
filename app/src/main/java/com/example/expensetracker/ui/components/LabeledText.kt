package com.example.expensetracker.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@Composable
fun LabeledText(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    valueColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium
) {
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(color = labelColor)) {
                append("$label : ")
            }
            withStyle(style = SpanStyle(color = valueColor)) {
                append(value)
            }
        },
        style = style,
        modifier = modifier
    )
}