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
import com.cjapps.prop.ui.invest.InvestScreen
import com.cjapps.prop.ui.invest.InvestViewModel
import com.cjapps.prop.ui.invest.summary.InvestResultSummary
import com.cjapps.prop.ui.invest.summary.InvestResultSummaryViewModel
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
                },
                navigateToInvestScreen = navigationActions.navigateToInvestScreen
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
        composable(
            route = PropDestinations.INVEST_ROUTE
        ) {
            val viewModel = hiltViewModel<InvestViewModel>()
            InvestScreen(
                investViewModel = viewModel,
                navigateHome = navigationActions.navigateHome,
                navigateToInvestResultSummary = navigationActions.navigateToInvestResultSummary
            )
        }
        composable(
            route = PropDestinations.INVEST_SUMMARY_ROUTE,
            arguments = listOf(navArgument("amountToInvest") {
                type = NavType.StringType; defaultValue = "0"
            })

        ) {
            val viewModel = hiltViewModel<InvestResultSummaryViewModel>()
            InvestResultSummary(
                viewModel = viewModel,
                navigateBack = navigationActions.navigateBack,
                navigateHome = navigationActions.navigateHome
            )
        }
    }
}