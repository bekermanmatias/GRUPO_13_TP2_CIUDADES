package com.example.misciudades.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.misciudades.data.CapitalRepository
import com.example.misciudades.screens.*

@Composable
fun CapitalNavGraph(
    repository: CapitalRepository,
    startDestination: String = "home"
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("home") {
            HomeScreen(
                repository = repository,
                onAdd = { navController.navigate("edit?mode=add&id=-1") },
                onEdit = { id -> navController.navigate("edit?mode=edit&id=$id") },
                onSearch = { navController.navigate("search") },
                onManageCountry = { navController.navigate("country") }
            )
        }

        composable(
            route = "edit?mode={mode}&id={id}",
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("id") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "add"
            val id = backStackEntry.arguments?.getInt("id") ?: -1
            EditCapitalScreen(
                repository = repository,
                mode = mode,
                capitalId = id,
                onSaved = { navController.popBackStack() }
            )
        }

        composable("search") {
            SearchCapitalScreen(
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }

        composable("country") {
            ManageCountryScreen(
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }
    }
}