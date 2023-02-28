package com.eddy.debuglibrary

import android.Manifest
import android.content.*
import android.os.IBinder
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresPermission
import com.eddy.debuglibrary.presentation.view.ui.overlay.OverlayTaskService
import com.eddy.debuglibrary.presentation.view.ui.permission.PermissionActivity
import com.eddy.debuglibrary.util.Constants
import com.eddy.debuglibrary.util.Constants.SharedPreferences.Companion.EDDY_DEBUG_TOOL_BOOLEAN_TOKEN
import com.eddy.debuglibrary.util.PermissionEventManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.greenrobot.eventbus.EventBus

@JvmSynthetic
public inline fun createDebugTool(
    context: Context,
    crossinline block: DebugTool.Builder.() -> Unit
): DebugTool = DebugTool.Builder(context).apply(block).build()

class DebugTool private constructor(
    private val context: Context,
    private val builder: Builder
) {

    private lateinit var myService: OverlayTaskService
    private var isService = false

    private val sharedPreferences = context.getSharedPreferences(Constants.SharedPreferences.EDDY_DEBUG_TOOL, Context.MODE_PRIVATE)

    private val permissionCallback by lazy {
        object : PermissionCallback {
            override fun allow() {
                Toast.makeText(context, "권한 허용", Toast.LENGTH_SHORT).show()
                startService()
            }

            override fun deny() {
                Toast.makeText(context, "권한 거절.", Toast.LENGTH_SHORT).show()
            }

            override fun neverDeny() {
                Toast.makeText(context, "권한 영구 거절.", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private val permissionEventManager by lazy { PermissionEventManager(permissionCallback) }

    @RequiresPermission(Manifest.permission.SYSTEM_ALERT_WINDOW)
    fun bindService() {
        if (getPermission()) {
            startService()
        }
    }

    fun unbindService() {
        if (isService) {
            context.unbindService(connection)
        }
    }

    private fun startService() {
        val intent = Intent(context, OverlayTaskService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun getPermission(): Boolean {

        val isNeverSeeAgain = sharedPreferences.getBoolean(EDDY_DEBUG_TOOL_BOOLEAN_TOKEN, false)

        if (builder.isAutoPermission && isNeverSeeAgain.not()) {
            if (!Settings.canDrawOverlays(context)) {
                val intent = Intent(context, PermissionActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                EventBus.getDefault().register(permissionEventManager)
                return false
            } else {
                EventBus.getDefault().unregister(permissionEventManager)
                return true
            }
        } else {
            if (!Settings.canDrawOverlays(context)) {
                Toast.makeText(context, "권한 을 먼저 얻어 주세요.", Toast.LENGTH_SHORT).show()
                return false
            } else {
                return true
            }
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as OverlayTaskService.OverlayDebugToolPopUpBinder
            isService = true

            myService = binder.getService()
            myService.setTagList(builder._searchKeyWords)
//            myService.setBackgroundColor(builder.backgroundColor)
//            myService.setLogFrom(builder._logForm)
//            myService.setSettingView(builder.isSettingView)
            myService.setUnBindServiceCallback(::unbindService)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isService = false
        }
    }

    public class Builder(private val context: Context) {

        @set:JvmSynthetic
        public var searchKeyWords: List<String>? = null

        internal val _searchKeyWords: MutableList<String>
            get() {
                return searchKeyWords?.let {
                    mutableListOf("normal").run {
                        addAll(it)
                        this
                    }
                } ?: mutableListOf("normal")

            }

        @set:JvmSynthetic
        public var isAutoPermission: Boolean = false

        @set:JvmSynthetic
        public var isSettingView: Boolean = false

//        @set:JvmSynthetic/**/
//        public var logLevel: LogLevel = LogLevel.D

//        internal val _logForm: List<LogForm> = listOf(LogForm(textColors, logLevel))


//        public fun setLogLevelTextColor(color: Int, logLevel: LogLevel): Builder = apply {
//            this.textColors = color
//            this.logLevel = logLevel
//        }

//        public fun setTextColors(values: List<LogForm>): Builder = apply {
//
//        }

        public fun setSearchKeyWord(value: String): Builder = apply {
            this.searchKeyWords = listOf(value)
        }

        public fun setSearchKeyWordList(values: List<String>): Builder = apply {
            this.searchKeyWords = values
        }

        public fun setJson(): Builder = apply {

        }

        public fun setJsonList(): Builder = apply {

        }

        public fun setSettingView(value: Boolean): Builder = apply {
            this.isSettingView = value
        }

//        public fun setBackgroundColor(@ColorInt value: Int): Builder = apply {
//            this.backgroundColor = value
//        }
//
//        public fun setBackgroundColorResource(@ColorRes value: Int): Builder = apply {
//            this.backgroundColor = ContextCompat.getColor(context, value)
//        }

        public fun setAutoPermissionCheck(value: Boolean): Builder = apply {
            this.isAutoPermission = value
        }

        public fun build(): DebugTool = DebugTool(
            context = context,
            builder = this@Builder
        )

    }

}