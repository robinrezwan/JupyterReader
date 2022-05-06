package com.robinrezwan.jupyterreader.ui.screens.notebook

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.robinrezwan.jupyterreader.ui.components.BackPressHandler
import com.robinrezwan.jupyterreader.util.CustomIcons
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NotebookTopFindBar(
    onValueChange: (String) -> Unit,
    onClickPrevious: () -> Unit,
    onClickNext: () -> Unit,
    onCancel: () -> Unit,
    activeMatch: Int,
    numberOfMatches: Int,
) {
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    val keyboardController = LocalSoftwareKeyboardController.current

    BackPressHandler(
        onBackPressed = {
            keyboardController?.hide()
            onCancel()
        }
    )

    Surface(
        modifier = Modifier.wrapContentHeight(),
        shadowElevation = 4.dp,
    ) {
        SmallTopAppBar(
            modifier = Modifier.statusBarsPadding(),
            title = {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp)
                        .height(52.dp)
                        .focusRequester(focusRequester),
                    placeholder = {
                        Text(
                            text = "Find...",
                            style = MaterialTheme.typography.labelLarge,
                        )
                    },
                    textStyle = MaterialTheme.typography.labelLarge,
                    value = text,
                    onValueChange = {
                        text = it
                        onValueChange(text)
                    },
                    singleLine = true,
                    shape = CircleShape,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { keyboardController?.hide() }
                    ),
                    trailingIcon = {
                        if (text.isNotEmpty()) {
                            Row(
                                modifier = Modifier.wrapContentWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "${activeMatch + (if (numberOfMatches != 0) 1 else 0)}/$numberOfMatches",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                IconButton(onClick = {
                                    text = ""
                                    onValueChange(text)
                                }) {
                                    Icon(CustomIcons.close(), null)
                                }
                            }
                        }
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                )
            },
            actions = {
                IconButton(
                    enabled = numberOfMatches != 0 && activeMatch > 0,
                    onClick = { onClickPrevious() }
                ) {
                    Icon(CustomIcons.chevronUp(), null)
                }

                IconButton(
                    enabled = numberOfMatches != 0 && activeMatch < numberOfMatches - 1,
                    onClick = { onClickNext() }
                ) {
                    Icon(CustomIcons.chevronDown(), null)
                }

                IconButton(
                    onClick = {
                        keyboardController?.hide()
                        onCancel()
                    }
                ) {
                    Icon(CustomIcons.close(), null)
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        delay(10)
        focusRequester.requestFocus()
        keyboardController?.show()
    }
}
