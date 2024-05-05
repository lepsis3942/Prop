package com.cjapps.prop.ui.invest.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.cjapps.prop.ui.composables.ListCard
import com.cjapps.prop.ui.extensions.fadingEdge
import com.cjapps.prop.ui.theme.ExtendedTheme
import com.cjapps.prop.ui.theme.ThemeDefaults
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestResultSummary(
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
                    onInvestTapped = viewModel::onInvestTapped,
                    tickerPriceErrorEncountered = stateSnapshot.tickerPriceErrorEncountered
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
    tickerPriceErrorEncountered: Boolean,
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
        if (tickerPriceErrorEncountered) {
            Card(
                modifier = Modifier
                    .padding(bottom = ThemeDefaults.pagePadding)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .weight(3.0f)
                            .height(18.dp),
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        modifier = Modifier.weight(7.0f),
                        text = "Failed to fetch ticker prices",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 1.0f),
        ) {
            val scrollState = rememberLazyListState()
            LazyColumn(
                modifier = Modifier.fadingEdge(scrollState),
                state = scrollState,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(items = investments) { investment ->
                    InvestmentItem(
                        modifier = Modifier
                            .fillMaxWidth(),
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
    ListCard(modifier = modifier, content = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ThemeDefaults.pagePadding),
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
        if (investment.shareInfo != null && investment.shareInfo.sharesToBuy > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = ThemeDefaults.pagePadding,
                        end = ThemeDefaults.pagePadding,
                        bottom = ThemeDefaults.pagePadding
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(weight = 2.0f)
                ) {
                    Text(
                        text = stringResource(id = R.string.investment_detail_at),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Column(
                        modifier = Modifier.padding(start = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.investment_detail_share_info_market_price),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = investment.shareInfo.marketPrice,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Text(
                    modifier = Modifier.weight(weight = 1.0f),
                    text = stringResource(id = R.string.investment_detail_share_info_buy),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.weight(weight = 2.0f),
                    text = stringResource(
                        id = R.string.investment_detail_share_info_shares,
                        investment.shareInfo.sharesToBuy
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    })
}