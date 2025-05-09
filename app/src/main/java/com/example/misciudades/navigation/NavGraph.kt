package com.example.misciudades.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
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

    NavHost(navController, startDestination) {
        composable("home") {
            HomeScreen(
                viewModel       = homeViewModel,
                onAdd           = { navController.navigate("edit?mode=add&id=-1") },
                onEdit          = { id -> navController.navigate("edit?mode=edit&id=$id") },
                onSearch        = { navController.navigate("search") },
                onManageCountry = { navController.navigate("country") }
            )
        }
        composable(
            "edit?mode={mode}&id={id}",
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("id")   { type = NavType.IntType }
            )
        ) { back ->
            val mode = back.arguments?.getString("mode") ?: "add"
            val id   = back.arguments?.getInt("id") ?: -1

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
        composable("search") {
            SearchCapitalScreen(
                repository = repository,
                onBack     = {
                    homeViewModel.loadAll()
                    navController.popBackStack()
                }
            )
        }
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
