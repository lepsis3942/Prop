package com.cjapps.prop

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

object PropDestinations {
    const val SUMMARY_ROUTE = "summary"
    const val CREATE_INVESTMENT_ROUTE = "createInvestment"
}

class PropNavigationActions(navController: NavController) {
    val navigateToInvestmentCreate: () -> Unit = {
        navController.navigate(PropDestinations.CREATE_INVESTMENT_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id)
            launchSingleTop = true
        }
    }

    val navigateHome: () -> Unit = {
        navController.navigate(PropDestinations.SUMMARY_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id)
            launchSingleTop = true
        }
    }
}