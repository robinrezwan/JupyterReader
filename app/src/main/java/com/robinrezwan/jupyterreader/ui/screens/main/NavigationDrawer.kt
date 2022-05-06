package com.robinrezwan.jupyterreader.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.robinrezwan.jupyterreader.R
import com.robinrezwan.jupyterreader.navigation.Route
import com.robinrezwan.jupyterreader.util.CustomIcons
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun AppNavDrawer(
    drawerState: DrawerState,
    navController: NavHostController,
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        modifier = Modifier.systemBarsPadding(),
        drawerShape = RectangleShape,
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.drawable.jupiter_planet),
                        modifier = Modifier.size(48.dp),
                        contentDescription = null,
                    )

                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            NavigationDrawerItem(
                modifier = Modifier.padding(horizontal = 4.dp),
                selected = false,
                icon = { Icon(CustomIcons.about(), null) },
                label = {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                onClick = {
                    navController.navigate(Route.About)
                    coroutineScope.launch { drawerState.close() }
                }
            )
        },
        content = content,
    )
}
