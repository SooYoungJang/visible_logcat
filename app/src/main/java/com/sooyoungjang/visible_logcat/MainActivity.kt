package com.sooyoungjang.visible_logcat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eddy.debuglibrary.createDebugTool

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createDebugTool(this) {
            setAutoPermissionCheck(true)
        }.bindService()
    }
}