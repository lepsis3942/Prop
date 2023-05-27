package com.cjapps.prop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cjapps.prop.models.InvestmentAllocation
import com.cjapps.prop.ui.extensions.asDisplayCurrency
import com.cjapps.prop.ui.extensions.asDisplayPercentage
import com.cjapps.prop.ui.theme.ListItemDividerColor
import com.cjapps.prop.ui.theme.Pink40
import com.cjapps.prop.ui.theme.Pink80
import com.cjapps.prop.ui.theme.PropComposeTheme
import com.cjapps.prop.ui.theme.Purple40
import com.cjapps.prop.ui.theme.Purple80
import com.cjapps.prop.ui.theme.ThemeDefaults
import java.math.BigDecimal

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
                totalForInvestedSum = investmentSummaryViewModel.totalForAllInvestments,
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
    totalForInvestedSum: BigDecimal,
    onAddInvestmentTap: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
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
    val colorList = if (isSystemInDarkTheme()) {
        listOf(Purple80, Pink80)
    } else {
        listOf(Purple40, Pink40)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                alpha = 0.5f
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
                    brush = Brush.linearGradient(colorList)
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