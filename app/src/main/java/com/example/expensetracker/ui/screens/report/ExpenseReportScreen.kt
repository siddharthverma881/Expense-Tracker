package com.example.expensetracker.ui.screens.report

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expensetracker.data.model.ExpenseCategory
import com.example.expensetracker.ui.theme.CategoryColors
import kotlin.math.max

@Composable
fun ExpenseReportScreen(
    navController: NavController,
    viewModel: ExpenseReportViewModel = hiltViewModel()
) {
    val dailyTotals = viewModel.getTotalForLast7Days()
    val categoryTotals = viewModel.getCategoryTotals()
    val dailyCategoryData = viewModel.getDailyCategoryTotals()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("7-Day Expense Report", style = MaterialTheme.typography.headlineSmall)

        // Category-aware bar chart
        CategoryBarChart(dailyCategoryData)

        CategoryLegend()

        Spacer(modifier = Modifier.height(16.dp))

        Text("Category Totals:", style = MaterialTheme.typography.titleMedium)
        categoryTotals.forEach { (category, total) ->
            Text("${category.name}: ₹$total")
        }
    }
}

@Composable
fun CategoryBarChart(dailyCategoryData: List<Map<ExpenseCategory, Double>>) {
    val maxVal = max(
        dailyCategoryData.maxOfOrNull { it.values.sum() } ?: 0.0,
        1.0
    )

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
                val totalForDay = categoryMap.values.sum()

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

        // Labels under bars
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            (1..dailyCategoryData.size).forEach { day ->
                Text(
                    text = "Day $day",
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