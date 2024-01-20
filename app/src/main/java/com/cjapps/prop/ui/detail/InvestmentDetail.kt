package com.cjapps.prop.ui.detail

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cjapps.prop.R
import com.cjapps.prop.ui.composables.animatingButtonColors
import com.cjapps.prop.ui.theme.ExtendedTheme
import com.cjapps.prop.ui.theme.ThemeDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentDetailScreen(
    modifier: Modifier = Modifier,
    investmentDetailViewModel: InvestmentDetailViewModel = viewModel(),
    navigateHome: () -> Unit
) {
    val uiState by investmentDetailViewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    if (uiState.saveCompleted == true || uiState.deleteCompleted == true) {
        navigateHome()
    }

    val errorMessage = when (uiState.errorState) {
        is ErrorUiState.DuplicateTickerError -> stringResource(id = R.string.investment_detail_error_duplicate_ticker)
        is ErrorUiState.UnknownError -> stringResource(id = R.string.generic_error)
        else -> null
    }

    if (errorMessage != null) {
        ErrorDialog(
            bodyMessage = errorMessage,
            onConfirmation = { investmentDetailViewModel.errorDialogConfirmed() },
            onDismissRequest = { investmentDetailViewModel.errorDialogConfirmed() }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            id = if (uiState.isUpdateMode)
                                R.string.investment_detail_page_title_update
                            else
                                R.string.investment_detail_page_title_create
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(id = R.string.page_back_content_description)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { investmentDetailViewModel.deleteInvestment() }) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = stringResource(
                                id = R.string.investment_detail_delete_button_content_desc
                            ),
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = ThemeDefaults.pagePadding)
                .fillMaxSize()
        ) {
            if (uiState.isLoading) {
                Row(
                    modifier = modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = stringResource(id = R.string.investment_detail_ticker_input_title)) },
                        value = uiState.tickerName,
                        onValueChange = { str: String ->
                            investmentDetailViewModel.updateTickerName(
                                str
                            )
                        },
                        shape = RoundedCornerShape(60.dp),
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        label = { Text(text = stringResource(id = R.string.investment_detail_current_value_input_title)) },
                        value = uiState.currentInvestmentValue,
                        onValueChange = { str: String ->
                            investmentDetailViewModel.updateCurrentValue(
                                str
                            )
                        },
                        visualTransformation = remember { CurrencyVisualTransformation() },
                        shape = RoundedCornerShape(60.dp),
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .weight(weight = 1.0f)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    focusManager.clearFocus()
                                }
                            )
                        }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Desired Percentage: ${uiState.currentPercentageToInvest}%",
                            modifier = Modifier.padding(bottom = 10.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                        DraggableNumberSelectionBar(
                            height = 200.dp,
                            width = 60.dp,
                            startingNumber = uiState.currentPercentageToInvest,
                            maxAllowedNumber = uiState.availablePercentageToInvest,
                            backgroundColor = ExtendedTheme.colors.inverseSecondary.copy(alpha = 0.3f),
                            fillBrush = Brush.linearGradient(ExtendedTheme.colors.gradientColorList),
                            numberSelectionUpdated = {
                                investmentDetailViewModel.updatePercentageToInvest(
                                    it
                                )
                            }
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, bottom = ThemeDefaults.pagePadding),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(fraction = 0.7f),
                        enabled = uiState.isSaveEnabled,
                        colors = animatingButtonColors(
                            buttonColors = ButtonDefaults.buttonColors(),
                            isButtonEnabled = uiState.isSaveEnabled
                        ),
                        onClick = {
                            investmentDetailViewModel.saveInvestmentAllocation()
                        }) {
                        Text(text = stringResource(id = R.string.investment_detail_save_button))
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorDialog(
    bodyMessage: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(
                onClick = { onConfirmation() }
            ) {
                Text(stringResource(id = R.string.ok))
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.investment_detail_error_dialog_title)
            )
        },
        text = { Text(text = bodyMessage) }
    )
}