import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.debuglibrary.R
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.SettingContract
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.model.LogKeywordModel
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.viewmodel.SettingViewModel
import kotlinx.coroutines.launch

@Composable
internal fun SettingScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel
) {
    val scope = rememberCoroutineScope()
    val state by viewModel.uiState.collectAsState()

    Column(Modifier.padding(5.dp)) {
        TitleAndCheckbox(text = state.backgroundTitle, isChecked = state.darkBackground, event = { viewModel.setEvent(SettingContract.Event.OnDarkBackgroundClick(it)) })
        Spacer(modifier = modifier.height(5.dp))
        TitleAndSpinner(
            text = state.textSizeTitle,
            state.curTextSizeValue,
            state.textSizeList,
            event = { viewModel.setEvent(SettingContract.Event.OnItemListSelectedPosition(it)) })
        Spacer(modifier = modifier.height(10.dp))
        TitleAndEditTextAndImageButton(text = state.filterKeywordTitle, event = { viewModel.setEvent(SettingContract.Event.OnAddFilterKeyword(it)) })
        Spacer(modifier = modifier.height(10.dp))
        TitleAndLazyColumn(
            text = state.filterKeywordListTitle,
            filterKeywords = state.filterKeywordModels,
            event = { viewModel.setEvent(SettingContract.Event.OnDeleteFilterKeyword(it)) })
    }
}

@Composable
internal fun TitleAndCheckbox(
    text: String,
    isChecked: Boolean,
    event: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(3.dp), text = text, fontSize = 15.sp
        )
        NoneTitleCheckBox(
            isChecked, event, modifier = Modifier
                .padding(end = 5.dp)
                .weight(2f)
        )
    }
}


@Composable
internal fun NoneTitleCheckBox(
    isChecked: Boolean,
    event: (Boolean) -> Unit,
    modifier: Modifier
) {
    val bgOnImage = painterResource(id = R.drawable.img_switch_on)
    val bgOffImage = painterResource(id = R.drawable.img_switch_off)

    Row(modifier = modifier, Arrangement.End) {
        if (isChecked) {
            Image(modifier = Modifier.clickable(true, onClick = { event.invoke(false) }), painter = bgOnImage, contentDescription = "switchOn")
        } else {
            Image(modifier = Modifier.clickable(true, onClick = { event.invoke(true) }), painter = bgOffImage, contentDescription = "switchOff")
        }
    }
}

@Composable
internal fun TitleAndSpinner(
    text: String,
    selectValue: String,
    textSizeList: List<String>,
    event: (Int) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(3.dp), text = text, fontSize = 15.sp
        )
        Spinner(selectValue, textSizeList, event)
    }
}

@Composable
internal fun Spinner(
    selectValue: String,
    items: List<String>,
    event: (Int) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(end = 5.dp)) {
        Button(onClick = { isExpanded = true }) {
            Text(text = selectValue)
        }

        DropdownMenu(
            modifier = Modifier.height(250.dp),
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(onClick = {
                    event.invoke(index)
                    isExpanded = false
                }) {
                    Text(text = item)
                }
            }
        }
    }
}

@Composable
internal fun TitleAndEditTextAndImageButton(
    text: String,
    event: (String) -> Unit
) {
    val textState = remember {
        mutableStateOf("")
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(3.dp), text = text, fontSize = 15.sp
        )
        TextField(
            modifier = Modifier
                .weight(1.3f)
                .defaultMinSize(minHeight = 30.dp),
            value = textState.value,
            onValueChange = { textState.value = it },
            singleLine = true,
            placeholder = { Text(text = stringResource(id = R.string.add_to_search_filter)) },
        )
        IconButton(modifier = Modifier
            .weight(0.3f)
            .size(30.dp), onClick = { event.invoke(textState.value) }) {
            Icon(painter = painterResource(id = R.drawable.add), contentDescription = "addToFilterKeyword")
        }
    }
}

@Composable
internal fun TitleAndLazyColumn(
    text: String,
    filterKeywords: List<LogKeywordModel>,
    event: (String) -> Unit
) {
    val reusableItemModifier = Modifier
        .size(30.dp)
        .padding(5.dp)

    val scrollState = rememberLazyListState()

    LaunchedEffect(filterKeywords) {
        if (filterKeywords.isNotEmpty()) scrollState.animateScrollToItem(0)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(3.dp), text = text, fontSize = 15.sp
        )
        Surface(color = colorResource(id = R.color.transparent_gray)) {
            LazyColumn(horizontalAlignment = Alignment.End, modifier = Modifier.height(118.dp).width(188.dp), state = scrollState) {
                items(items = filterKeywords, key = { it.content }) { item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = item.content, textAlign = TextAlign.Center, modifier = Modifier.padding(end = 5.dp))
                        if (item.content != "normal") IconButton(onClick = { event.invoke(item.content) }, modifier = reusableItemModifier) {
                            Icon(painter = painterResource(id = R.drawable.trash_can), contentDescription = "deleteIcon")
                        }
                    }
                }
            }
        }

    }
}