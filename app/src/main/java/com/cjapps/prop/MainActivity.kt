package com.cjapps.prop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cjapps.prop.models.InvestmentAllocation
import com.cjapps.prop.ui.extensions.asDisplayCurrency
import com.cjapps.prop.ui.extensions.asDisplayPercentage
import com.cjapps.prop.ui.theme.ExtendedTheme
import com.cjapps.prop.ui.theme.PropComposeTheme
import com.cjapps.prop.ui.theme.ThemeDefaults
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PropComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    InvestmentSummaryScreen()
                }
            }
        }
    }
}

@Composable
fun InvestmentSummaryScreen(
    modifier: Modifier = Modifier,
    investmentSummaryViewModel: InvestmentSummaryViewModel = viewModel()
) {
    val uiState by investmentSummaryViewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        AppTitleHeader(
            modifier
        )
        if (uiState.isLoading) {
            Row(
                modifier = modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator()
            }
        } else {
            HeaderCard(
                accountTotal = uiState.totalForAllInvestments,
                onAddInvestmentTap = { investmentSummaryViewModel.onAddInvestmentTapped() },
                onInvestTap = { investmentSummaryViewModel.onInvestTapped() },
            )
            InvestmentAllocations(
                modifier = Modifier

                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 16.dp),
                investmentAllocations = uiState.investmentAllocations,
                totalForInvestedSum = uiState.totalForAllInvestments,
            )
        }
    }
}

@Composable
fun AppTitleHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = ThemeDefaults.pagePadding,
                end = ThemeDefaults.pagePadding,
                top = ThemeDefaults.pagePadding,
                bottom = ThemeDefaults.pagePadding + 16.dp
            ),
    ) {
        Text(
            text = "Prop",
            style = ThemeDefaults.appTitleTextStyle
        )
    }
}

@Composable
fun HeaderCard(
    modifier: Modifier = Modifier,
    accountTotal: BigDecimal,
    onAddInvestmentTap: () -> Unit,
    onInvestTap: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ThemeDefaults.pagePadding),
        colors = CardDefaults.elevatedCardColors(),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Column(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth()
        ) {
            val moneyTextStyle = MaterialTheme.typography.titleLarge.copy(fontSize = 26.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = "Total:", style = moneyTextStyle)
                Text(
                    text = accountTotal.asDisplayCurrency(),
                    style = moneyTextStyle
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                FilledTonalButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp), onClick = onAddInvestmentTap
                ) {
                    Text(text = "Add Stock", style = MaterialTheme.typography.titleMedium)
                }
                FilledTonalButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp), onClick = onInvestTap
                ) {
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = "Invest", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Icon(
                        Icons.Rounded.ArrowForward,
                        contentDescription = "Invest"
                    )
                }
            }
        }
    }
}

@Composable
fun InvestmentAllocations(
    modifier: Modifier = Modifier,
    investmentAllocations: List<InvestmentAllocation>,
    totalForInvestedSum: BigDecimal
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(investmentAllocations.size, itemContent = { index ->
            InvestmentGridCard(
                investmentName = investmentAllocations[index].tickerName,
                investmentPercentage = investmentAllocations[index].realPercentage(
                    totalForInvestedSum
                ),
                amount = investmentAllocations[index].currentInvestedAmount
            )
        })
    }
}

@Composable
fun InvestmentGridCard(
    investmentName: String,
    investmentPercentage: BigDecimal,
    amount: BigDecimal
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                alpha = 0.4f
            )
        )
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = investmentName,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
            )
            Text(
                text = investmentPercentage.asDisplayPercentage(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 40.sp,
                    brush = Brush.linearGradient(ExtendedTheme.colors.gradientColorList)
                )
            )
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = amount.asDisplayCurrency(),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}