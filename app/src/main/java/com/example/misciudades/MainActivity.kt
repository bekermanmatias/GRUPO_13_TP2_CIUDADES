package com.example.misciudades

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.misciudades.data.CapitalRepository
import com.example.misciudades.navigation.CapitalNavGraph
import com.example.misciudades.screens.HomeViewModel
import com.example.misciudades.ui.theme.MisCiudadesTheme

class MainActivity : ComponentActivity() {
    private lateinit var repository: CapitalRepository
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository    = CapitalRepository(this)
        homeViewModel = HomeViewModel(repository)

        setContent {
            MisCiudadesTheme {
                CapitalNavGraph(
                    repository    = repository,
                    homeViewModel = homeViewModel
                )
            }
        }
    }
}
