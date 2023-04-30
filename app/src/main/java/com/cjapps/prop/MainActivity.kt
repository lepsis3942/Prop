package com.cjapps.prop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cjapps.prop.models.InvestmentAllocation
import com.cjapps.prop.ui.extensions.asDisplayCurrency
import com.cjapps.prop.ui.extensions.asDisplayPercentage
import com.cjapps.prop.ui.theme.ListItemDividerColor
import com.cjapps.prop.ui.theme.PropComposeTheme
import com.cjapps.prop.ui.theme.Typography
import com.cjapps.prop.ui.theme.ThemeDefaults

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
    Column(modifier = modifier.fillMaxSize()) {
        AppTitleHeader(
            modifier
        )
        if (investmentSummaryViewModel.investmentAllocations.isEmpty()) {
            EmptyInvestmentState(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onAddInvestmentTap = { investmentSummaryViewModel.onAddInvestmentTapped() }
            )
        } else {
            InvestmentAllocations(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                investmentAllocations = investmentSummaryViewModel.investmentAllocations,
                onAddInvestmentTap = { investmentSummaryViewModel.onAddInvestmentTapped() }
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
            .padding(horizontal = ThemeDefaults.pagePadding, vertical = 16.dp),
    ) {
        Text(
            text = "Prop",
            style = ThemeDefaults.appTitleTextStyle
        )
    }
}

@Composable
fun EmptyInvestmentState(modifier: Modifier = Modifier, onAddInvestmentTap: () -> Unit) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AddInvestmentButton(onAddInvestmentTap = onAddInvestmentTap)
    }
}

@Composable
fun AddInvestmentButton(onAddInvestmentTap: () -> Unit) {
    ElevatedButton(onClick = onAddInvestmentTap) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Add Investment",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        Text(text = "Add Investment")
    }
}

@Composable
fun InvestmentAllocations(
    modifier: Modifier = Modifier,
    investmentAllocations: List<InvestmentAllocation>,
    onAddInvestmentTap: () -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(investmentAllocations.size + 1, itemContent = { index ->
            if (index != investmentAllocations.size) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ThemeDefaults.pagePadding)
                ) {
                    Text(
                        text = investmentAllocations[index].tickerName,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(modifier = Modifier.weight(0.1f))
                        AllocationPercentageDisplay(
                            modifier = Modifier.weight(1f).defaultMinSize(minHeight = 100.dp),
                            percentage = investmentAllocations[index].desiredPercentage
                        )
                        Box(modifier = Modifier.weight(0.2f))
                        AllocationCurrentAmount(
                            modifier = Modifier.weight(1f).defaultMinSize(minHeight = 100.dp),
                            amount = investmentAllocations[index].currentInvestedAmount
                        )
                        Box(modifier = Modifier.weight(0.1f))
                    }
                }
            } else {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AddInvestmentButton(onAddInvestmentTap = onAddInvestmentTap)
                }
            }
            if (index <= investmentAllocations.size - 1) ListDivider()
        })
    }
}

@Composable
fun AllocationPercentageDisplay(
    modifier: Modifier = Modifier,
    percentage: BigDecimal
) {
    RoundedCornerBox(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = percentage.asDisplayPercentage(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun AllocationCurrentAmount(
    modifier: Modifier = Modifier,
    amount: BigDecimal
) {
    RoundedCornerBox(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = amount.asDisplayCurrency(),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun RoundedCornerBox(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable (modifier: Modifier) -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(8.dp),
        contentAlignment = contentAlignment
    ) {
        content(modifier)
    }
}

@Composable
fun ListDivider(modifier: Modifier = Modifier) {
    return Box(
        modifier = modifier
            .height(1.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .background(ListItemDividerColor)
    )
}