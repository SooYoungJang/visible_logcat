package com.sooyoungjang.debuglibrary.presentation.view.ui.permission.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sooyoungjang.debuglibrary.presentation.view.ui.permission.PermissionContract
import com.sooyoungjang.debuglibrary.presentation.view.ui.permission.viewmodel.PermissionViewModel

@Composable
internal fun PermissionScreen(
    viewModel: PermissionViewModel
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(5.dp))
        Text(state.title)

        Spacer(modifier = Modifier.height(5.dp))
        Button(onClick = { viewModel.setEvent(PermissionContract.Event.OnConfirmClick) }) {
            Text(text = state.confirmTitle)
        }

        Spacer(modifier = Modifier.height(5.dp))
        Button(onClick = { viewModel.setEvent(PermissionContract.Event.OnCancelClick) }) {
            Text(text = state.cancelTitle)
        }

        Spacer(modifier = Modifier.height(5.dp))
        Button(onClick = { viewModel.setEvent(PermissionContract.Event.OnNeverSeeAgainClick) }) {
            Text(text = state.neverSeeAgainTitle)
        }
    }
}