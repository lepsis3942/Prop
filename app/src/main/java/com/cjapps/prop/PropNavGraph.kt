package com.cjapps.prop

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cjapps.prop.ui.summary.InvestmentSummaryScreen
import com.cjapps.prop.ui.summary.InvestmentSummaryViewModel

@Composable
fun PropNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = PropDestinations.summary) {
        composable(
            route = PropDestinations.summary
        ) {
            val viewModel = hiltViewModel<InvestmentSummaryViewModel>()
            InvestmentSummaryScreen(investmentSummaryViewModel = viewModel)
        }
    }
}