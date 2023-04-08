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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cjapps.prop.models.InvestmentAllocation
import com.cjapps.prop.ui.extensions.asDisplayCurrency
import com.cjapps.prop.ui.extensions.asDisplayPercentage
import com.cjapps.prop.ui.theme.ListItemDividerColor
import com.cjapps.prop.ui.theme.PropComposeTheme
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
        InvestmentTotalSummary(
            modifier,
            investmentSummaryViewModel.totalForAllInvestments,
            investmentSummaryViewModel.amountToInvestTextState
        )
        Text(
            text = "Investments",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
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
                allInvestmentsTotal = investmentSummaryViewModel.totalForAllInvestments,
                onAddInvestmentTap = { investmentSummaryViewModel.onAddInvestmentTapped() }
            )
        }
//        Box(
//            modifier = Modifier
//                .background(color = Color.Blue)
//                .height(50.dp)
//                .fillMaxWidth()
//        )
    }
}

@Composable
fun InvestmentTotalSummary(
    modifier: Modifier = Modifier,
    currentTotal: BigDecimal,
    amountToInvestText: MutableState<String>
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Current Total",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(
                    bottom = 4.dp
                )
            )
            Text(text = currentTotal.asDisplayCurrency())
        }
//        OutlinedTextField(
//            modifier = Modifier
//                .weight(1f)
//                .padding(horizontal = 20.dp),
//            value = amountToInvestText.value,
//            onValueChange = { amountToInvestText.value = it },
//            label = { Text("Amount to Invest") }
//        )
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
    allInvestmentsTotal: BigDecimal,
    onAddInvestmentTap: () -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(investmentAllocations.size + 1, itemContent = { index ->
            if (index != investmentAllocations.size) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 8.dp)
                ) {
                    Text(
                        text = investmentAllocations[index].tickerName,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp, start = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(modifier = Modifier.weight(0.1f))
                        RoundedCornerBox(modifier = Modifier.weight(1f)) {
                            Column {
                                Text(
                                    text = "Ideal",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = investmentAllocations[index].desiredPercentage.asDisplayPercentage(),
                                )
                                Text(
                                    text = investmentAllocations[index].currentInvestedAmount.asDisplayCurrency(),
                                )
                            }
                        }
                        Box(modifier = Modifier.weight(0.2f))
                        RoundedCornerBox(modifier = Modifier.weight(1f)) {
                            Column {
                                Text(
                                    text = investmentAllocations[index].desiredPercentage.asDisplayPercentage(),
                                )
                                Text(
                                    text = investmentAllocations[index].currentInvestedAmount.asDisplayCurrency(),
                                )
                            }
                        }
                        Box(modifier = Modifier.weight(0.1f))
                    }
                }
            } else {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center) {
                    AddInvestmentButton(onAddInvestmentTap = onAddInvestmentTap)
                }
            }
            if (index <= investmentAllocations.size - 1) ListDivider()
        })
    }
}

@Composable
fun RoundedCornerBox(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    content: @Composable (modifier: Modifier) -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(8.dp)
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