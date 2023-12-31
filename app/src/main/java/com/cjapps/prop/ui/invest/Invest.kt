package com.cjapps.prop.ui.invest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cjapps.prop.R
import com.cjapps.prop.ui.detail.CurrencyVisualTransformation
import com.cjapps.prop.ui.theme.ExtendedTheme
import com.cjapps.prop.ui.theme.ThemeDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestScreen(
    modifier: Modifier = Modifier,
    investViewModel: InvestViewModel = viewModel(),
    navigateHome: () -> Unit
) {
    val uiState by investViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.invest_page_title)) },
                navigationIcon = {
                    IconButton(onClick = navigateHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(id = R.string.page_back_content_description)
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        when (val frozenState = uiState) {
            InvestScreenUiState.Loading -> {
                Row(
                    modifier = modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                }
            }

            is InvestScreenUiState.AdjustingValues -> AdjustInvestmentValues(
                modifier = modifier,
                paddingValues = paddingValues,
                amountToInvest = frozenState.amountToInvest,
                investments = frozenState.investments,
                updateAmountToInvest = investViewModel::updateAmountToInvest,
                updateInvestmentCurrentAmount = investViewModel::updateInvestmentCurrentAmount,
                investTapped = investViewModel::investTapped
            )

            is InvestScreenUiState.CalculationComplete -> InvestmentCalculationComplete(
                modifier = modifier,
                paddingValues = paddingValues,
                amountToInvest = frozenState.amountToInvest,
                investments = frozenState.investments
            )
        }
    }
}

@Composable
private fun AdjustInvestmentValues(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    amountToInvest: String,
    investments: List<InvestmentScreenCurrentInvestmentValue>,
    updateAmountToInvest: (String) -> Unit,
    updateInvestmentCurrentAmount: (Int, String) -> Unit,
    investTapped: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .imePadding()
            .padding(horizontal = ThemeDefaults.pagePadding)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 32.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                label = { Text(text = stringResource(id = R.string.invest_amount_to_invest_input_title)) },
                value = amountToInvest,
                onValueChange = { str: String ->
                    updateAmountToInvest(str)
                },
                visualTransformation = remember { CurrencyVisualTransformation() },
                shape = RoundedCornerShape(60.dp),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 1.0f),
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                items(items = investments) { investment ->
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        label = { Text(text = investment.investmentName) },
                        value = investment.investmentValue,
                        onValueChange = { str: String ->
                            updateInvestmentCurrentAmount(
                                investment.id,
                                str
                            )
                        },
                        visualTransformation = remember { CurrencyVisualTransformation() },
                        shape = RoundedCornerShape(60.dp),
                    )
                }
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
                onClick = {
                    investTapped()
                }) {
                Text(text = stringResource(id = R.string.invest_invest_button))
            }
        }
    }
}

@Composable
fun InvestmentCalculationComplete(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    amountToInvest: String,
    investments: List<InvestmentScreenUpdatedInvestmentValue>
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = ThemeDefaults.pagePadding)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 32.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(id = R.string.invest_amount_to_invest_title),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = amountToInvest,
                    color = ExtendedTheme.colors.currencyGreen,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 1.0f),
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                items(items = investments) { investment ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = ThemeDefaults.pagePadding),
//                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(
                            modifier = Modifier.weight(weight = 2.0f),
                            text = investment.investmentName,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        Icon(
                            modifier = Modifier.weight(weight = 1.0f),
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = stringResource(id = R.string.invest_calculation_arrow_content_description)
                        )
                        Text(
                            modifier = Modifier.weight(weight = 2.0f),
                            text = investment.amountToInvest,
                            color = ExtendedTheme.colors.currencyGreen,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
