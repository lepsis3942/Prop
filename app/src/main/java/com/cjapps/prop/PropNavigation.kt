package com.cjapps.prop

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.cjapps.prop.util.withNavParameters

object PropDestinations {
    const val SUMMARY_ROUTE = "summary"
    const val CREATE_INVESTMENT_ROUTE = "investment/create"
    const val UPDATE_INVESTMENT_ROUTE = "investment/{investmentId}"
    const val INVEST_ROUTE = "invest"
}

class PropNavigationActions(navController: NavController) {
    val navigateHome: () -> Unit = {
        navController.navigate(PropDestinations.SUMMARY_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id)
            launchSingleTop = true
        }
    }

    val navigateToInvestmentCreate: () -> Unit = {
        navController.navigate(PropDestinations.CREATE_INVESTMENT_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id)
            launchSingleTop = true
        }
    }

    val navigateToInvestmentUpdate: (investmentId: Int) -> Unit = { id ->
        navController.navigate(
            PropDestinations.UPDATE_INVESTMENT_ROUTE.withNavParameters(
                mapOf("investmentId" to id.toString())
            )
        ) {
            popUpTo(navController.graph.findStartDestination().id)
            launchSingleTop = true
        }
    }

    val navigateToInvestScreen: () -> Unit = {
        navController.navigate(
            PropDestinations.INVEST_ROUTE
        ) {
            popUpTo(navController.graph.findStartDestination().id)
            launchSingleTop = true
        }
    }
}