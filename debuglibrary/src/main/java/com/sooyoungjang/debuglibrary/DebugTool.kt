package com.sooyoungjang.debuglibrary

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.provider.Settings
import android.widget.Toast
import com.sooyoungjang.debuglibrary.presentation.view.ui.overlay.OverlayTaskService
import com.sooyoungjang.debuglibrary.presentation.view.ui.permission.PermissionActivity
import com.sooyoungjang.debuglibrary.util.Constants
import com.sooyoungjang.debuglibrary.util.Constants.SharedPreferences.Companion.EDDY_DEBUG_TOOL_BOOLEAN_TOKEN
import com.sooyoungjang.debuglibrary.util.PermissionEventManager
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

    fun bindService() {
        if (getPermission()) startService()

    }

    fun unbindService() {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = manager.getRunningServices(Integer.MAX_VALUE)

        val isMyServiceRunning = services.any { it.service.className == OverlayTaskService::class.java.name }
        if (isMyServiceRunning)  context.unbindService(connection)
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

            myService = binder.getService()
            myService.setUnBindServiceCallback(::unbindService)
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }

    public class Builder(private val context: Context) {
        @set:JvmSynthetic
        public var isAutoPermission: Boolean = false

        public fun setJson(): Builder = apply {

        }

        public fun setJsonList(): Builder = apply {

        }

        public fun setAutoPermissionCheck(value: Boolean): Builder = apply {
            this.isAutoPermission = value
        }

        public fun build(): DebugTool = DebugTool(
            context = context,
            builder = this@Builder
        )

    }

}