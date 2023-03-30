import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.debuglibrary.R
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.SettingContract
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.model.LogKeywordModel
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.viewmodel.SettingViewModel

@Composable
internal fun SettingScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel
) {
    val scope = rememberCoroutineScope()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

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
                .padding(3.dp), text = text, fontSize = 15.sp,
        )
        NoneTitleCheckBox(
            isChecked, event,  modifier = Modifier
                .padding(end = 5.dp)
                .weight(2f),
            onImage = R.drawable.img_switch_on,
            offImage = R.drawable.img_switch_off
        )
    }
}


@Composable
internal fun NoneTitleCheckBox(
    isChecked: Boolean,
    event: (Boolean) -> Unit,
    @DrawableRes onImage: Int,
    @DrawableRes offImage: Int,
    modifier: Modifier = Modifier
) {
    val bgOnImage = painterResource(id = onImage)
    val bgOffImage = painterResource(id = offImage)

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
        Spinner(selectValue = selectValue, items =  textSizeList, event = event)
    }
}

@Composable
internal fun Spinner(
    modifier: Modifier = Modifier,
    selectValue: String,
    items: List<String>,
    event: (Int) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(end = 5.dp)) {
        Button(onClick = { isExpanded = true }) {
            Text(text = selectValue, overflow = TextOverflow.Ellipsis, modifier = Modifier.sizeIn(maxHeight = 30.dp, maxWidth = 80.dp))
        }
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(text = { Text(text = item) }, onClick = {
                    event.invoke(index)
                    isExpanded = false
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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

    var isExpandable by remember { mutableStateOf(false) }

    LaunchedEffect(filterKeywords) {
        if (filterKeywords.isNotEmpty()) scrollState.scrollToItem(0)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(3.dp), text = text, fontSize = 15.sp
        )
        Surface(color = colorResource(id = R.color.transparent_gray)) {
            LazyColumn(
                horizontalAlignment = Alignment.End, modifier = Modifier
                    .height(118.dp)
                    .width(188.dp),
                state = scrollState
            ) {
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