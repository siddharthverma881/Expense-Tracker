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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expensetracker.data.model.ExpenseCategory
import com.example.expensetracker.ui.components.DropdownMenuBox
import com.example.expensetracker.util.Utils.amountRegex
import kotlinx.coroutines.launch
import com.airbnb.lottie.compose.*
import com.example.expensetracker.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ExpenseEntryScreen(
    navController: NavController,
    viewModel: ExpenseEntryViewModel = hiltViewModel()
) {
    // Local UI state
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(ExpenseCategory.Food) }
    var notes by remember { mutableStateOf("") }
    var receiptImageUri by remember { mutableStateOf<String?>(null) } // Mock image as URI string

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val totalSpentToday by viewModel.totalSpentToday.collectAsState()

    var animateAlpha by remember { mutableStateOf(1f) }

    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success_animation))
    var showSuccessAnimation by remember { mutableStateOf(false) }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = showSuccessAnimation,
        iterations = 1,
        speed = 1.0f,
        restartOnPlay = true
    )

    // Detect when animation completes
    LaunchedEffect(progress, showSuccessAnimation) {
        if (showSuccessAnimation && progress == 1f) {
            showSuccessAnimation = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Add Expense", style = MaterialTheme.typography.headlineSmall)

            Text(
                text = "Total Spent Today: ₹${"%.2f".format(totalSpentToday)}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.alpha(animateAlpha)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { input ->
                        if (input.matches(amountRegex)) {
                            amount = input
                        }
                    },
                    label = { Text("Amount (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                DropdownMenuBox(
                    selected = category,
                    onCategorySelected = { category = it }
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { if (it.length <= 100) notes = it },
                    label = { Text("Notes (optional)") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mock Receipt Image upload button + preview
                Button(
                    onClick = {
                        // For now just toggle mock URI to simulate picking image
                        receiptImageUri = if (receiptImageUri == null) "mock_uri" else null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (receiptImageUri == null) "Add Receipt Image" else "Remove Receipt Image")
                }

                receiptImageUri?.let {
                    // Just a placeholder box representing the image
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

            Button(
                onClick = {
                    keyboardController?.hide()
                    if(title.trim().isEmpty()){
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please enter title")
                        }
                    } else if(amount.trim().isEmpty()){
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please enter amount")
                        }
                    } else if(amount.trim().toDouble() <= 0){
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please enter valid amount")
                        }
                    } else {
                        coroutineScope.launch {
                            viewModel.addExpense(
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                category = category,
                                description = notes,
                                title = title,
                                date = System.currentTimeMillis()
                            )
                            // Clear fields immediately behind animation
                            title = ""
                            amount = ""
                            notes = ""
                            category = ExpenseCategory.Food
                            receiptImageUri = null

                            // Show success animation immediately
                            showSuccessAnimation = true

                            // Show snackbar AFTER animation finishes or in parallel if you prefer:
//                            snackbarHostState.showSnackbar("Expense added")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
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
                    progress = progress,
                    modifier = Modifier.size(150.dp)
                )
            }
        }
    }
}
