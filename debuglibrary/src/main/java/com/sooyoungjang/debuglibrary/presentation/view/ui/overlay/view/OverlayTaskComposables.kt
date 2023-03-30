package com.sooyoungjang.debuglibrary.presentation.view.ui.overlay.view

import NoneTitleCheckBox
import Spinner
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.debuglibrary.R
import com.sooyoungjang.debuglibrary.presentation.view.ui.overlay.OverlayTaskContract
import com.sooyoungjang.debuglibrary.presentation.view.ui.overlay.viewmodel.OverlayTaskViewModel

@Composable
internal fun OverlayTaskScreen(
    viewModel: OverlayTaskViewModel,
    rootViewListener: (Float, Float) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.expandView) {
        ExpandMenu(state, viewModel, rootViewListener)
    } else {
        FoldMenu(viewModel, rootViewListener)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ExpandMenu(
    state: OverlayTaskContract.State,
    viewModel: OverlayTaskViewModel,
    rootViewListener: (Float, Float) -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val scrollState = rememberLazyListState()

    LaunchedEffect(state.scrollPosition) {
        if (state.logs.isNotEmpty()) {
//            scrollState.animateScrollToItem(0) //가장 아래 스크롤 해두었으면 스크롤 자동 스크롤 하게 해주기 아니면 아니고.
            scrollState.scrollToItem(state.scrollPosition)
        }
    }

    Box(Modifier.wrapContentSize()) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column() {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(modifier = Modifier.weight(1f), onClick = { viewModel.setEvent(OverlayTaskContract.Event.OnNavigateToSetting) }) {
                        Icon(painter = painterResource(id = R.drawable.setting), contentDescription = "setting icon button")
                    }
                    Spinner(
                        selectValue = state.filterKeywordTitle,
                        items = state.filterKeywordList,
                        event = { viewModel.setEvent(OverlayTaskContract.Event.OnKeywordItemClick(it)) })
                    IconButton(modifier = Modifier.weight(1f), onClick = { viewModel.setEvent(OverlayTaskContract.Event.OnNavigateToSearchIng) }) {
                        Icon(painter = painterResource(id = R.drawable.search), contentDescription = "searching icon button")
                    }
                    IconButton(modifier = Modifier.weight(1f), onClick = { viewModel.setEvent(OverlayTaskContract.Event.DeleteLog) }) {
                        Icon(painter = painterResource(id = R.drawable.trash_can), contentDescription = "trash icon button")
                    }
                    NoneTitleCheckBox(
                        modifier = Modifier.weight(1f),
                        isChecked = state.zoom, event = { viewModel.setEvent(OverlayTaskContract.Event.OnZoomLog(it)) },
                        onImage = R.drawable.reduction,
                        offImage = R.drawable.expand
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.move), contentDescription = "move Icon Button",
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectDragGestures() { change, dragAmount ->
                                    offsetX += dragAmount.x
                                    offsetY += dragAmount.y
                                    rootViewListener.invoke(offsetX, offsetY)
                                    change.consume()
                                }
                            }
                            .weight(1f)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.close), contentDescription = "close icon button",
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 3.dp)
                            .combinedClickable(
                                onClick = { viewModel.setEvent(OverlayTaskContract.Event.OnCloseClick) },
                                onLongClick = { viewModel.setEvent(OverlayTaskContract.Event.OnCloseLongClick) }
                            )
                    )
                }

                Surface() {
                        LazyColumn(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier
                                .height(state.zoomHeight.dp)
                                .background(color = colorResource(id = state.backgroundColor)),
                            state = scrollState,
                        ) {
                            itemsIndexed(items = state.logs) { index, item ->
                                Card(modifier = if(index == state.scrollPosition) Modifier
                                    .background(color = colorResource(id = R.color.purple_500))
                                    .padding(3.dp) else Modifier.padding(3.dp)) {
                                    Text(text = item.content, textAlign = TextAlign.Center, modifier = Modifier.padding(end = 5.dp))
                                }
                            }
                        }
                }

                if(state.searching) {
                    Surface() {
                        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = state.searchKeyword, textAlign = TextAlign.Center, modifier = Modifier.weight(2f))
                            IconButton(modifier = Modifier.weight(1f), onClick = { viewModel.setEvent(OverlayTaskContract.Event.OnPageUpClick(state.searchKeyword, scrollState.firstVisibleItemIndex) )}) {
                                Icon(painter = painterResource(id = R.drawable.up_arrow), contentDescription = "up arrow")
                            }
                            IconButton(modifier = Modifier.weight(1f), onClick = { viewModel.setEvent(OverlayTaskContract.Event.OnPageDownClick(state.searchKeyword, scrollState.firstVisibleItemIndex) )}) {
                                Icon(painter = painterResource(id = R.drawable.down_arrow), contentDescription = "down arrow")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun FoldMenu(
    viewModel: OverlayTaskViewModel,
    rootViewListener: (Float, Float) -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(Modifier.wrapContentSize()) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(8.dp),
        ) {
            Row(
                Modifier.align(Alignment.Center),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { viewModel.setEvent(OverlayTaskContract.Event.OnOpenClick) },
                ) {
                    Text(stringResource(id = R.string.log))
                }
                Icon(painter = painterResource(id = R.drawable.move), contentDescription = "move Icon Button",
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { },
                                onDragEnd = { /* 드래그 종료 시 호출 */ },
                                onDragCancel = { /* 드래그 취소 시 호출 */ }
                            ) { change, dragAmount ->
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                                rootViewListener.invoke(offsetX, offsetY)
                                change.consume()
                            }
                        }
                )
            }
        }
    }
}