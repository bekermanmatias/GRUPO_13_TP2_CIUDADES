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
    homeViewModel: HomeViewModel,
    startDestination: String = "home"
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        // 1) Home
        composable("home") {
            HomeScreen(
                viewModel       = homeViewModel,
                onAdd           = { navController.navigate("edit?mode=add&id=-1") },
                onEdit          = { id -> navController.navigate("edit?mode=edit&id=$id") },
                onSearch        = { navController.navigate("search") },
                onManageCountry = { navController.navigate("country") }
            )
        }

        // 2) Edit/Add screen
        composable(
            route = "edit?mode={mode}&id={id}",
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("id")   { type = NavType.IntType }
            )
        ) { backStack ->
            val mode = backStack.arguments?.getString("mode") ?: "add"
            val id   = backStack.arguments?.getInt("id") ?: -1

            EditCapitalScreen(
                repository = repository,
                mode       = mode,
                capitalId  = id,
                onSaved    = {
                    homeViewModel.loadAll()
                    navController.popBackStack()
                }
            )
        }

        // 3) Search screen
        composable("search") {
            SearchCapitalScreen(
                repository = repository,
                onBack     = {
                    homeViewModel.loadAll()
                    navController.popBackStack()
                }
            )
        }

        // 4) Manage country screen
        composable("country") {
            ManageCountryScreen(
                repository = repository,
                onBack     = {
                    homeViewModel.loadAll()
                    navController.popBackStack()
                }
            )
        }
    }
}
