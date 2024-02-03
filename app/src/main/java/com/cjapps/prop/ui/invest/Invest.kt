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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cjapps.prop.R
import com.cjapps.prop.ui.composables.animatingButtonColors
import com.cjapps.prop.ui.detail.CurrencyVisualTransformation
import com.cjapps.prop.ui.extensions.fadingEdge
import com.cjapps.prop.ui.theme.ThemeDefaults
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestScreen(
    modifier: Modifier = Modifier,
    investViewModel: InvestViewModel = viewModel(),
    navigateHome: () -> Unit,
    navigateToInvestResultSummary: (String) -> Unit
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
        when (val stateSnapshot = uiState) {
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
                paddingValues = paddingValues,
                amountToInvest = stateSnapshot.amountToInvest,
                isInvestEnabled = stateSnapshot.investEnabled,
                investments = stateSnapshot.investments,
                updateAmountToInvest = investViewModel::updateAmountToInvest,
                updateInvestmentCurrentAmount = investViewModel::updateInvestmentCurrentAmount,
                investTapped = investViewModel::investTapped
            )

            is InvestScreenUiState.InvestRequested -> {
                navigateToInvestResultSummary(stateSnapshot.amountToInvest)
                investViewModel.navigationComplete()
            }
        }
    }
}

@Composable
private fun AdjustInvestmentValues(
    paddingValues: PaddingValues,
    amountToInvest: String,
    isInvestEnabled: Boolean,
    investments: ImmutableList<InvestmentScreenCurrentInvestmentValue>,
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
            val scrollState = rememberLazyListState()
            LazyColumn(
                modifier = Modifier.fadingEdge(scrollState),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                state = scrollState
            ) {
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
                colors = animatingButtonColors(
                    buttonColors = ButtonDefaults.buttonColors(),
                    isButtonEnabled = isInvestEnabled
                ),
                enabled = isInvestEnabled,
                onClick = {
                    investTapped()
                }) {
                Text(text = stringResource(id = R.string.invest_invest_button))
            }
        }
    }
}