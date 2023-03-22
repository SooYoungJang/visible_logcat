package com.sooyoungjang.visible_logcat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.sooyoungjang.debuglibrary.createDebugTool
import kotlinx.coroutines.delay
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createDebugTool(this) {
            setAutoPermissionCheck(true)
        }.bindService()

        thread(start = true) {
            repeat(15) {
                Thread.sleep(700)
                Log.d("Test","eddy lkjlkjlkjlkj $it")
            }
        }
    }
}