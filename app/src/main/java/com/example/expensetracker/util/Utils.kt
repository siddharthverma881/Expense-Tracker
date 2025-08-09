package com.example.expensetracker.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Utils {

    // Allow only digits and max one decimal point
    val amountRegex = Regex("^\\d*\\.?\\d*\$")

    fun formatDate(millis: Any): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd MMM yy")
            val localDate = Instant.ofEpochMilli(millis.toString().toLong()).atZone(ZoneId.systemDefault()).toLocalDate()
            localDate.format(formatter)
        } catch (e: Exception){
            millis.toString()
        }
    }

    @Composable
    fun AnnotatedString.getCustomString(key: String, value: String): AnnotatedString {
        return buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Black)) {
                append("$key: ")
            }
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                append(value)
            }
        }
    }
}