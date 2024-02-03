package com.cjapps.prop.ui.invest.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cjapps.prop.R
import com.cjapps.prop.ui.theme.ExtendedTheme
import com.cjapps.prop.ui.theme.ThemeDefaults
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestResultSummary(
    modifier: Modifier = Modifier,
    viewModel: InvestResultSummaryViewModel,
    navigateBack: () -> Unit,
    navigateHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.invest_result_page_title)) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
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
            is InvestResultScreenUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is InvestResultScreenUiState.CalculationComplete -> {
                InvestmentCalculationComplete(
                    paddingValues = paddingValues,
                    amountToInvest = stateSnapshot.amountToInvest,
                    investments = stateSnapshot.investments,
                    onInvestTapped = viewModel::onInvestTapped
                )
            }

            InvestResultScreenUiState.InvestmentsSaved -> navigateHome()
        }
    }
}

@Composable
fun InvestmentCalculationComplete(
    paddingValues: PaddingValues,
    amountToInvest: String,
    investments: ImmutableList<InvestmentScreenUpdatedInvestmentValue>,
    onInvestTapped: () -> Unit
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
                    InvestmentItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = ThemeDefaults.pagePadding),
                        investment = investment
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
                onClick = onInvestTapped
            ) {
                Text(text = stringResource(id = R.string.investment_detail_save_button))
            }
        }
    }
}

@Composable
fun InvestmentItem(
    modifier: Modifier = Modifier,
    investment: InvestmentScreenUpdatedInvestmentValue
) {
    Row(
        modifier = modifier,
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