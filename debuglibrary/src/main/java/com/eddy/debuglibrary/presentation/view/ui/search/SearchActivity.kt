package com.eddy.debuglibrary.presentation.view.ui.search

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.eddy.debuglibrary.presentation.view.ui.overlay.OverlayTaskService
import com.eddy.debuglibrary.presentation.view.ui.overlay.OverlayTaskService.Companion.SEARCH_KEYWORD
import com.example.debuglibrary.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    private var _binding: ActivitySearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        setFinishOnTouchOutside(false)
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val editText = EditText(this)
        val dialog = AlertDialog.Builder(this)
            .setTitle("검색어를 입력해 주세요.")
            .setView(editText)
            .setPositiveButton("확인") { dialog, which ->
                val intent = Intent(this, OverlayTaskService::class.java)
                intent.putExtra(SEARCH_KEYWORD,editText.text.toString())
                startService(intent)

                finish()
            }

        val alertDialog: AlertDialog = dialog.create()
        alertDialog.window!!.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        alertDialog.show()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}