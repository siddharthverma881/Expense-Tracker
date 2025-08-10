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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expensetracker.R
import com.example.expensetracker.data.model.ExpenseCategory
import com.example.expensetracker.ui.theme.CategoryColors
import com.example.expensetracker.viewmodel.ExpenseReportViewModel
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.max

/**
 * Expense Report screen.
 * Shows 7-day expense trends with:
 * - Stacked bar chart (daily category data)
 * - Category legend
 * - Daily totals
 * - Category totals
 * Includes option to export data as CSV and share it.
 */
@Composable
fun ExpenseReportScreen(
    viewModel: ExpenseReportViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Observing report data from ViewModel
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

            // Chart section
            if (dailyCategoryData.isNotEmpty()) {
                CategoryBarChart(dailyCategoryData.reversed())
                CategoryLegend()
            } else {
                Text(stringResource(id = R.string.empty_report_message), color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Daily Totals section
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Daily Totals:", style = MaterialTheme.typography.titleMedium)
                    dailyTotals.toSortedMap().forEach { (date, total) ->
                        Text(
                            "${date.format(DateTimeFormatter.ofPattern("dd MMM"))}: ₹${"%.2f".format(total)}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // Category Totals section
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Category Totals:", style = MaterialTheme.typography.titleMedium)
                    categoryTotals.forEach { (category, total) ->
                        Text(
                            "${category.name}: ₹${"%.2f".format(total)}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Floating Action Button for exporting CSV
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
            text = { Text(stringResource(id = R.string.export_text)) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

/**
 * Exports expense report data as a CSV file and triggers a share intent.
 *
 * @param context Application context for file and intent handling
 * @param dailyCategoryData List of maps containing daily expense amounts by category
 * @param categoryTotals Total expense amount for each category over the report period
 */
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

        // Add daily data rows
        dailyCategoryData.reversed().forEachIndexed { index, dayData ->
            val date = today.minusDays((dailyCategoryData.size - 1 - index).toLong())
            sb.append(date.format(dateFormatter))
            ExpenseCategory.values().forEach { category ->
                val amount = dayData[category] ?: 0.0
                sb.append(",${"%.2f".format(amount)}")
            }
            sb.append("\n")
        }

        // Category totals
        sb.append("\nCategory Totals\n")
        ExpenseCategory.values().forEach { category ->
            val total = categoryTotals[category] ?: 0.0
            sb.append("${category.name},${"%.2f".format(total)}\n")
        }

        // Grand total
        val totalWeeklyExpense = categoryTotals.values.sum()
        sb.append("\nTotal Weekly Expense,${"%.2f".format(totalWeeklyExpense)}\n")

        // Write CSV file to cache
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

        // Create and launch share intent
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(
            Intent.createChooser(shareIntent, "Share Expense Report CSV")
        )
    } catch (e: Exception) {
        Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

/**
 * Draws a stacked bar chart representing daily expense distribution by category.
 *
 * @param dailyCategoryData List of daily category-to-amount mappings
 */
@Composable
fun CategoryBarChart(dailyCategoryData: List<Map<ExpenseCategory, Double>>) {
    val maxVal = max(
        dailyCategoryData.maxOfOrNull { it.values.sum() } ?: 0.0,
        1.0
    )
    val today = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd")

    Column {
        // Canvas for drawing stacked bars
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

        // X-axis labels (dates)
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

/**
 * Displays a legend mapping colors to expense categories.
 */
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