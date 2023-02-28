package com.eddy.debuglibrary.presentation.view.ui.setting

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.eddy.debuglibrary.util.Constants
import com.eddy.debuglibrary.util.Constants.SharedPreferences.Companion.EDDY_LOG_TEXT_SIZE
import com.eddy.debuglibrary.util.Constants.SharedPreferences.Companion.EDDY_SETTING_BACKGROUND
import com.example.debuglibrary.R
import com.example.debuglibrary.databinding.ActivitySettingBinding
import org.greenrobot.eventbus.EventBus


internal class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(Constants.SharedPreferences.EDDY_DEBUG_TOOL, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        supportActionBar?.title = getString(R.string.setting)
        setContentView(binding.root)

        setupCheckbox()
        setupSpinner()
        setupFilterTextView()
    }

    private fun setupCheckbox() {
        binding.cbBackground.isChecked = sharedPreferences.getBoolean(EDDY_SETTING_BACKGROUND, false)
        binding.cbBackground.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean(EDDY_SETTING_BACKGROUND, isChecked).apply()
        }
    }

    private fun setupSpinner() {
        val textSizes = resources.getStringArray(R.array.text_size_array)
        binding.spTextSize.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, textSizes)
        binding.spTextSize.setSelection(sharedPreferences.getInt(EDDY_LOG_TEXT_SIZE, 10))
        binding.spTextSize.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sharedPreferences.edit().putInt(EDDY_LOG_TEXT_SIZE, position).apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

    }

    private fun setupFilterTextView() {
        binding.etInputFilterKeyword.setOnKeyListener { v, keyCode, event ->
            when(keyCode) {
                KeyEvent.KEYCODE_ENTER -> {
                    binding.tvFilterKeyword.text = "ssss"
                    return@setOnKeyListener true
                }

                else -> { return@setOnKeyListener false}
            }
        }
    }

    override fun onBackPressed() {
        EventBus.getDefault().post(SettingEvent.OnBackPress)
        super.onBackPressed()
    }
}