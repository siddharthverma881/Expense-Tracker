package com.example.expensetracker.ui.screens.report

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expensetracker.data.model.ExpenseCategory
import com.example.expensetracker.ui.theme.CategoryColors
import kotlinx.coroutines.flow.forEach
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.max

@Composable
fun ExpenseReportScreen(
    navController: NavController,
    viewModel: ExpenseReportViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val dailyCategoryData by viewModel.dailyCategoryData.collectAsState()
    val categoryTotals by viewModel.categoryTotals.collectAsState()
    val dailyTotals by viewModel.dailyTotals.collectAsState()

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("7-Day Expense Report", style = MaterialTheme.typography.headlineSmall)

            if (dailyCategoryData.isNotEmpty()) {
                CategoryBarChart(dailyCategoryData.reversed())
                CategoryLegend()
            } else {
                Text("No data available", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Daily Totals section
            Text("Daily Totals:", style = MaterialTheme.typography.titleMedium)
            dailyTotals.toSortedMap().forEach { (date, total) ->
                Text(
                    "${date.format(DateTimeFormatter.ofPattern("dd MMM"))}: ₹${"%.2f".format(total)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Category Totals:", style = MaterialTheme.typography.titleMedium)
            categoryTotals.forEach { (category, total) ->
                Text(
                    "${category.name}: ₹${"%.2f".format(total)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

//            Button(
//                onClick = {
//                    exportAndShareCSV(context, dailyCategoryData, categoryTotals)
//                },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Export Report as CSV")
//            }


        }
        // Floating Action Button
        ExtendedFloatingActionButton(
            onClick = {
                exportAndShareCSV(context, dailyCategoryData, categoryTotals)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share CSV"
                )
            },
            text = { Text("Export") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}


private fun exportAndShareCSV(
    context: Context,
    dailyCategoryData: List<Map<ExpenseCategory, Double>>,
    categoryTotals: Map<ExpenseCategory, Double>
) {
    try {
        // Build CSV content
        val dateFormatter = DateTimeFormatter.ofPattern("MMM dd")
        val today = LocalDate.now()

        val sb = StringBuilder()
        sb.append("Date")
        ExpenseCategory.values().forEach { sb.append(",${it.name}") }
        sb.append("\n")

        dailyCategoryData.reversed().forEachIndexed { index, dayData ->
            val date = today.minusDays((dailyCategoryData.size - 1 - index).toLong())
            sb.append(date.format(dateFormatter))
            ExpenseCategory.values().forEach { category ->
                val amount = dayData[category] ?: 0.0
                sb.append(",${"%.2f".format(amount)}")
            }
            sb.append("\n")
        }

        sb.append("\nCategory Totals\n")
        ExpenseCategory.values().forEach { category ->
            val total = categoryTotals[category] ?: 0.0
            sb.append("${category.name},${"%.2f".format(total)}\n")
        }

        // Calculate total weekly expense (sum of all category totals)
        val totalWeeklyExpense = categoryTotals.values.sum()
        sb.append("\nTotal Weekly Expense,${"%.2f".format(totalWeeklyExpense)}\n")

        // Write CSV to cache file
        val fileName = "expense_report.csv"
        val file = File(context.cacheDir, fileName)
        FileOutputStream(file).use { fos ->
            fos.write(sb.toString().toByteArray())
        }

        // Get content URI for file using FileProvider
        val contentUri: Uri = FileProvider.getUriForFile(
            context,
            context.packageName + ".fileprovider",
            file
        )

        // Create share intent
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Start share intent
        context.startActivity(
            Intent.createChooser(shareIntent, "Share Expense Report CSV")
        )
    } catch (e: Exception) {
        Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

@Composable
fun CategoryBarChart(dailyCategoryData: List<Map<ExpenseCategory, Double>>) {
    val maxVal = max(
        dailyCategoryData.maxOfOrNull { it.values.sum() } ?: 0.0,
        1.0
    )
    val today = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd")

    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(vertical = 8.dp)
        ) {
            val barWidth = size.width / (dailyCategoryData.size * 2)

            dailyCategoryData.forEachIndexed { index, categoryMap ->
                var accumulatedHeight = 0f

                categoryMap.forEach { (category, amount) ->
                    val barHeight = ((amount / maxVal) * size.height).toFloat()

                    drawRoundRect(
                        color = CategoryColors[category] ?: Color.Gray,
                        topLeft = androidx.compose.ui.geometry.Offset(
                            x = index * (barWidth * 2) + barWidth / 2,
                            y = size.height - accumulatedHeight - barHeight
                        ),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(4f, 4f)
                    )

                    accumulatedHeight += barHeight
                }
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            dailyCategoryData.forEachIndexed { index, _ ->
                val date = today.minusDays((dailyCategoryData.size - 1 - index).toLong())
                Text(
                    text = date.format(dateFormatter),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryLegend() {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CategoryColors.forEach { (category, color) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(color)
                )
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}