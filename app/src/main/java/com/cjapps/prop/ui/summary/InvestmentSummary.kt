package com.cjapps.prop.ui.summary

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cjapps.prop.R
import com.cjapps.prop.models.InvestmentAllocation
import com.cjapps.prop.ui.extensions.asDisplayCurrency
import com.cjapps.prop.ui.extensions.asDisplayPercentage
import com.cjapps.prop.ui.extensions.isNumericalValueEqualTo
import com.cjapps.prop.ui.theme.ExtendedTheme
import com.cjapps.prop.ui.theme.ThemeDefaults
import java.math.BigDecimal

@Composable
fun InvestmentSummaryScreen(
    modifier: Modifier = Modifier,
    investmentSummaryViewModel: InvestmentSummaryViewModel = viewModel(),
    navigateToInvestmentCreate: () -> Unit,
    navigateToInvestmentUpdate: (Int) -> Unit,
    navigateToInvestScreen: () -> Unit
) {
    val uiState by investmentSummaryViewModel.uiState.collectAsStateWithLifecycle()

    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
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
                    modifier.padding(bottom = 16.dp),
                    accountTotal = uiState.totalForAllInvestments,
                    onAddInvestmentTap = { navigateToInvestmentCreate() },
                    investButtonEnabled = uiState.isInvestButtonEnabled,
                    onInvestTap = { navigateToInvestScreen() },
                )
                PercentageProgressIndicator(
                    modifier = Modifier.padding(
                        start = ThemeDefaults.pagePadding,
                        end = ThemeDefaults.pagePadding,
                        bottom = 13.dp
                    ),
                    currentProgress = uiState.totalAllocatedPercent,
                    backgroundColor = MaterialTheme.colorScheme.errorContainer,
                    barHeight = 40.dp,
                    fillBrush = Brush.linearGradient(ExtendedTheme.colors.gradientColorList),
                )
                InvestmentAllocations(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    investmentAllocations = uiState.investmentAllocations,
                    accountTotal = uiState.totalForAllInvestments,
                    onInvestmentTapped = { id -> navigateToInvestmentUpdate(id) }
                )
            }
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
            text = "Prop", style = ThemeDefaults.appTitleTextStyle
        )
    }
}

@Composable
fun HeaderCard(
    modifier: Modifier = Modifier,
    accountTotal: BigDecimal,
    investButtonEnabled: Boolean,
    onAddInvestmentTap: () -> Unit,
    onInvestTap: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ThemeDefaults.pagePadding),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
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
                Text(text = accountTotal.asDisplayCurrency(), style = moneyTextStyle)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp), onClick = onAddInvestmentTap
                ) {
                    Text(text = "Add Stock", style = MaterialTheme.typography.titleMedium)
                }
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    enabled = investButtonEnabled,
                    onClick = onInvestTap,
                ) {
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = "Invest", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = "Invest"
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
    accountTotal: BigDecimal,
    onInvestmentTapped: (Int) -> Unit
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
                investmentId = investmentAllocations[index].id ?: 0,
                investmentName = investmentAllocations[index].tickerName,
                currentPercentage = investmentAllocations[index].realPercentage(accountTotal),
                desiredPercentage = investmentAllocations[index].desiredPercentage,
                amount = investmentAllocations[index].currentInvestedAmount,
                onTap = onInvestmentTapped
            )
        })
    }
}

@Composable
fun InvestmentGridCard(
    investmentId: Int,
    investmentName: String,
    currentPercentage: BigDecimal,
    desiredPercentage: BigDecimal,
    amount: BigDecimal,
    onTap: (Int) -> Unit
) {
    val isCurrentPercentageSuccessState =
        currentPercentage > desiredPercentage || currentPercentage.isNumericalValueEqualTo(
            desiredPercentage
        )
    val desiredPercentageColor = if (isCurrentPercentageSuccessState) {
        ExtendedTheme.colors.currencyGreen
    } else {
        MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .pointerInput(Unit) {
                detectTapGestures { onTap(investmentId) }
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                alpha = 0.4f
            )
        )
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = investmentName,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
                )
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = stringResource(id = R.string.investment_summary_edit_investment_content_desc),
                    modifier = Modifier.size(13.dp)
                )
            }
            Row {
                Text(
                    modifier = Modifier.alignByBaseline(),
                    text = desiredPercentage.divide(BigDecimal(100)).asDisplayPercentage(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 40.sp,
                        brush = Brush.linearGradient(ExtendedTheme.colors.gradientColorList)
                    )
                )
                PercentageWithArrow(
                    modifier = Modifier
                        .alignByBaseline()
                        .padding(start = 10.dp),
                    percentage = currentPercentage,
                    textStyle = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 16.sp,
                        color = desiredPercentageColor
                    ),
                    displayArrow = currentPercentage > desiredPercentage && !currentPercentage.isNumericalValueEqualTo(
                        desiredPercentage
                    ),
                    arrowUp = currentPercentage > desiredPercentage,
                    arrowContentDescription = stringResource(id = R.string.investment_summary_current_percentage_content_desc)
                )
            }
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = amount.asDisplayCurrency(),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun PercentageWithArrow(
    modifier: Modifier,
    percentage: BigDecimal,
    textStyle: TextStyle,
    displayArrow: Boolean,
    arrowUp: Boolean,
    arrowContentDescription: String
) {
    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        Text(
            text = percentage.divide(BigDecimal(100)).asDisplayPercentage(),
            style = textStyle
        )
        if (displayArrow) {
            Icon(
                imageVector = if (arrowUp) Icons.Rounded.ArrowDropDown else Icons.Rounded.ArrowDropDown,
                contentDescription = arrowContentDescription,
                modifier = Modifier
                    .size(14.dp)
                    .rotate(if (arrowUp) 180f else 0f),
                tint = textStyle.color
            )
        }
    }
}