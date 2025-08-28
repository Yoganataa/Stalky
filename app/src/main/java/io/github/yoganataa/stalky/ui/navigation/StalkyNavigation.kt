package io.github.yoganataa.stalky.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.github.yoganataa.stalky.ui.screens.main.MainScreen
import io.github.yoganataa.stalky.ui.screens.sources.SourcesScreen
import io.github.yoganataa.stalky.ui.screens.editor.ScriptEditorScreen

@Composable
fun StalkyNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(
                onNavigateToManga = { manga ->
                    navController.navigate("manga/${manga.id}")
                },
                onNavigateToSources = {
                    navController.navigate("sources")
                }
            )
        }
        
        composable("sources") {
            SourcesScreen(
                onNavigateBack = { navController.popBackStack() },
                onEditSource = { source ->
                    navController.navigate("editor/${source.id}")
                }
            )
        }
        
        composable("editor/{sourceId}") { backStackEntry ->
            val sourceId = backStackEntry.arguments?.getString("sourceId") ?: ""
            ScriptEditorScreen(
                sourceId = sourceId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}