package com.arcx.uniwall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arcx.network.KtorClient
import com.arcx.network.domain.Photo
import com.arcx.uniwall.screens.MainScreen
import com.arcx.uniwall.screens.PhotoScreen
import com.arcx.uniwall.ui.theme.UniwallTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var ktorClient: KtorClient

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()

            var photos by remember {
                mutableStateOf<List<Photo>>(listOf())
            }

            LaunchedEffect(Unit) {
                ktorClient
                    .searchPhotos("universe", 2)
                    .onSuccess {
                        photos = it
                    }
            }

            UniwallTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(navController = navController, startDestination = "main", modifier = Modifier.padding(innerPadding)) {
                        composable(route = "main") {
                            MainScreen(
                                onClick = {
                                    navController.navigate("photo/$it")
                                }
                            )
                        }

                        composable(route = "photo/{id}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            PhotoScreen(id = id)
                        }
                    }
                }
            }
        }
    }
}