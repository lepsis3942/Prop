package com.cjapps.prop

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cjapps.prop.ui.detail.InvestmentDetailScreen
import com.cjapps.prop.ui.detail.InvestmentDetailViewModel
import com.cjapps.prop.ui.summary.InvestmentSummaryScreen
import com.cjapps.prop.ui.summary.InvestmentSummaryViewModel

@Composable
fun PropNavGraph(
    navController: NavHostController = rememberNavController(),
    navigationActions: PropNavigationActions
) {
    NavHost(navController = navController, startDestination = PropDestinations.SUMMARY_ROUTE) {
        composable(
            route = PropDestinations.SUMMARY_ROUTE
        ) {
            val viewModel = hiltViewModel<InvestmentSummaryViewModel>()
            InvestmentSummaryScreen(
                investmentSummaryViewModel = viewModel,
                navigateToInvestmentCreate = navigationActions.navigateToInvestmentCreate,
                navigateToInvestmentUpdate = { investmentId ->
                    navigationActions.navigateToInvestmentUpdate(
                        investmentId
                    )
                }
            )
        }
        composable(
            route = PropDestinations.CREATE_INVESTMENT_ROUTE,
        ) {
            val viewModel = hiltViewModel<InvestmentDetailViewModel>()
            InvestmentDetailScreen(
                investmentDetailViewModel = viewModel,
                navigateHome = navigationActions.navigateHome
            )
        }
        composable(
            route = PropDestinations.UPDATE_INVESTMENT_ROUTE,
            arguments = listOf(navArgument("investmentId") { type = NavType.IntType })
        ) {
            val viewModel = hiltViewModel<InvestmentDetailViewModel>()
            InvestmentDetailScreen(
                investmentDetailViewModel = viewModel,
                navigateHome = navigationActions.navigateHome
            )
        }
    }
}