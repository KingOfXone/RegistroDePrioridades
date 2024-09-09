package edu.ucne.prioridades.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import edu.ucne.prioridades.data.database.PrioridadDb
import edu.ucne.prioridades.presentation.navigation.prioridad.DeletePrioridadScreen
import edu.ucne.prioridades.presentation.navigation.prioridad.PrioridadEditScreen
import edu.ucne.prioridades.presentation.navigation.prioridad.PrioridadListScreen
import edu.ucne.prioridades.presentation.navigation.prioridad.PrioridadScreen


@Composable
fun PrioridadNavHost(
    prioridadDb: PrioridadDb,
    navHostController: NavHostController
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val prioridadList by prioridadDb.prioridadDAO().getAll()
        .collectAsStateWithLifecycle(
            initialValue = emptyList(),
            lifecycleOwner = lifecycleOwner,
            minActiveState = Lifecycle.State.STARTED
        )

    NavHost(
        navController = navHostController,
        startDestination = Screen.PrioridadList
    ) {
        composable<Screen.PrioridadList> {
            PrioridadListScreen(
                prioridadList,
                createPrioridad = {
                    navHostController.navigate(Screen.Prioridad(0))
                },
                onEditPrioridad = { prioridad ->
                    navHostController.navigate(Screen.PrioridadEdit(prioridad.prioridadId!!))
                },
                onDeletePrioridad = { prioridad ->
                    navHostController.navigate(Screen.PrioridadDelete(prioridad.prioridadId!!))
                }
            )
        }
        composable<Screen.Prioridad> { backStackEntry ->
            PrioridadScreen(
                prioridadDb,
                goBack = {
                    navHostController.navigateUp()
                }
            )
        }
        composable<Screen.PrioridadEdit> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.PrioridadEdit>()
            PrioridadEditScreen(
                prioridadDb,
                prioridadId = args.prioridadId,
                goBack = {
                    navHostController.navigateUp()
                }
            )
        }
        composable<Screen.PrioridadDelete> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.PrioridadDelete>()
            DeletePrioridadScreen(
                prioridadDb,
                prioridadId = args.prioridadId,
                goBack = {
                    navHostController.navigateUp()
                }
            )
        }
    }
}

