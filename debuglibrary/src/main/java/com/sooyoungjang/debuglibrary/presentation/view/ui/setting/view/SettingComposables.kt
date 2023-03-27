import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.viewmodel.SettingViewModel


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
internal fun SettingScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel
) {

    val scope = rememberCoroutineScope()

    Row() {
        Row(horizontalArrangement = Arrangement.Start) {
//            Text(text = R.string.change_background_color)
        }

        Row(horizontalArrangement = Arrangement.Start) {

        }

        Row(horizontalArrangement = Arrangement.Start) {

        }

        Row(horizontalArrangement = Arrangement.Start) {

        }

        Row(horizontalArrangement = Arrangement.Start) {

        }

        Row(horizontalArrangement = Arrangement.Start) {

        }
    }

}

@Composable
fun Counter(count: Int, updateCount: (Int) -> Unit) {
    Button(
        onClick = { updateCount(count + 1) },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (count > 5) Color.Cyan else Color.White
        )
    ) {
        Text("$count 번 클릭하셨네요!")
    }
}