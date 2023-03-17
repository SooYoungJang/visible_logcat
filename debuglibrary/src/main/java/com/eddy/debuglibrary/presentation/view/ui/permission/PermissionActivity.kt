package com.eddy.debuglibrary.presentation.view.ui.permission

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eddy.debuglibrary.di.AppContainer
import com.eddy.debuglibrary.di.DiManager
import com.example.debuglibrary.databinding.ActivityPermissionBinding
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

internal class PermissionActivity : AppCompatActivity() {

    private var _binding: ActivityPermissionBinding? = null
    private val binding get() = _binding!!
    private val appContainer: AppContainer by lazy { DiManager.getInstance(this).appContainer }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        setFinishOnTouchOutside(false)
        super.onCreate(savedInstanceState)

        _binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConfirm.setOnClickListener {
            checkPermission()
        }

        binding.btnCancel.setOnClickListener {
            EventBus.getDefault().post(PermissionEvent.Deny)
            finish()
        }

        binding.btnNeverSeeAgain.setOnClickListener {
            lifecycleScope.launch {
                appContainer.writeDataStoreUseCase.run(true)
                EventBus.getDefault().post(PermissionEvent.NeverDeny)
                finish()
            }
        }
    }

    private fun checkPermission() {
        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")).run { childForResult.launch(this) }
    }

    private val childForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (!Settings.canDrawOverlays(this)) {
                EventBus.getDefault().post(PermissionEvent.Deny)
            } else {
                EventBus.getDefault().post(PermissionEvent.Allow)
                finish()
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}