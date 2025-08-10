package com.example.expensetracker.ui.screens.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expensetracker.data.model.ExpenseCategory
import com.example.expensetracker.ui.components.DropdownMenuBox
import com.example.expensetracker.util.Utils.amountRegex
import kotlinx.coroutines.launch
import com.airbnb.lottie.compose.*
import com.example.expensetracker.R
import com.example.expensetracker.viewmodel.ExpenseEntryViewModel

/**
 * Screen for entering a new expense.
 * Features:
 * - Form with Title, Amount, Category, Notes, and optional Receipt Image
 * - Validation before saving
 * - Displays today's total spent
 * - Success animation after saving
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ExpenseEntryScreen(
    viewModel: ExpenseEntryViewModel = hiltViewModel()
) {
    // Local form state
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(ExpenseCategory.Food) }
    var notes by remember { mutableStateOf("") }
    var receiptImageUri by remember { mutableStateOf<String?>(null) } // Mock URI for receipt

    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val totalSpentToday by viewModel.totalSpentToday.collectAsState()

    val animateAlpha by remember { mutableFloatStateOf(1f) }

    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Lottie success animation setup
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success_animation))
    var showSuccessAnimation by remember { mutableStateOf(false) }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = showSuccessAnimation,
        iterations = 1,
        speed = 1.0f,
        restartOnPlay = true
    )

    // Hide success animation after it finishes playing
    LaunchedEffect(progress, showSuccessAnimation) {
        if (showSuccessAnimation && progress == 1f) {
            showSuccessAnimation = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Screen title
            Text("Add Expense", style = MaterialTheme.typography.headlineSmall)

            // Display today's total spent
            Text(
                text = "Total Spent Today: ₹${"%.2f".format(totalSpentToday)}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.alpha(animateAlpha)
            ) {
                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(id = R.string.expense_entry_title)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Amount field (numeric only)
                OutlinedTextField(
                    value = amount,
                    onValueChange = { input ->
                        if (input.matches(amountRegex)) {
                            amount = input
                        }
                    },
                    label = { Text(stringResource(id = R.string.expense_entry_amount)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Category dropdown
                DropdownMenuBox(
                    selected = category,
                    onCategorySelected = { category = it }
                )

                // Notes field (max 100 chars)
                OutlinedTextField(
                    value = notes,
                    onValueChange = { if (it.length <= 100) notes = it },
                    label = { Text(stringResource(id = R.string.expense_entry_notes)) },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mock receipt image upload/remove button
                Button(
                    onClick = {
                        receiptImageUri = if (receiptImageUri == null) "mock_uri" else null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (receiptImageUri == null)
                            stringResource(id = R.string.receipt_add_message)
                        else
                            stringResource(id = R.string.receipt_remove_message)
                    )
                }

                // Receipt preview (mock)
                receiptImageUri?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(top = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Receipt Image Preview (mock)", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pre-fetch snackbar messages from strings.xml
            val titleEmptyMessage = stringResource(id = R.string.title_empty_message)
            val amountEmptyMessage = stringResource(id = R.string.amount_empty_message)
            val invalidAmountMessage = stringResource(id = R.string.invalid_amount_message)

            // Submit button with validation
            Button(
                onClick = {
                    keyboardController?.hide()
                    when {
                        title.trim().isEmpty() -> {
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar(message = titleEmptyMessage)
                            }
                        }
                        amount.trim().isEmpty() -> {
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar(message = amountEmptyMessage)
                            }
                        }
                        amount.trim().toDouble() <= 0 -> {
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar(message = invalidAmountMessage)
                            }
                        }
                        else -> {
                            coroutineScope.launch {
                                // Add expense to DB
                                viewModel.addExpense(
                                    amount = amount.toDoubleOrNull() ?: 0.0,
                                    category = category,
                                    description = notes,
                                    title = title,
                                    date = System.currentTimeMillis()
                                )
                                // Clear form fields
                                title = ""
                                amount = ""
                                notes = ""
                                category = ExpenseCategory.Food
                                receiptImageUri = null

                                // Trigger success animation
                                showSuccessAnimation = true
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.submit_expense_btn_text))
            }
        }

        // Success animation overlay
        if (showSuccessAnimation) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(150.dp)
                )
            }
        }
    }
}
