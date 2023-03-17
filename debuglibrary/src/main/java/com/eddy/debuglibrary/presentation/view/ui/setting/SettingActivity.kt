package com.eddy.debuglibrary.presentation.view.ui.setting

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.eddy.debuglibrary.di.AppContainer
import com.eddy.debuglibrary.presentation.view.ui.setting.epoxy.LogKeywordController
import com.eddy.debuglibrary.presentation.view.ui.setting.epoxy.LogKeywordModel
import com.eddy.debuglibrary.presentation.view.ui.setting.viewmodel.SettingViewModel
import com.eddy.debuglibrary.util.Constants
import com.eddy.debuglibrary.util.Constants.SharedPreferences.Companion.EDDY_LOG_FILTER_KEYWORD
import com.example.debuglibrary.R
import com.example.debuglibrary.databinding.ActivitySettingBinding
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus


internal class SettingActivity : AppCompatActivity() {

    private var _binding: ActivitySettingBinding? = null
    private val binding get() = _binding!!

    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(Constants.SharedPreferences.EDDY_DEBUG_TOOL, Context.MODE_PRIVATE)
    }

    private val appContainer: AppContainer by lazy { AppContainer(this) }
    private val controller: LogKeywordController by lazy { LogKeywordController() }
    private val viewModel: SettingViewModel by lazy { SettingViewModel(appContainer.sharedPreferencesUtil) }
    private var arrayListPrefs = ArrayList<String>() // 저장할 ArrayList
    private var stringPrefs : String? = null // 저장할 때 사용할 문자열 변수

    var userSelect = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingBinding.inflate(layoutInflater)
        supportActionBar?.title = getString(R.string.setting)
        setContentView(binding.root)

        setupSpinner()
        binding.cbBackground.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setEvent(SettingContract.Event.OnDarkBackgroundClick(isChecked))
        }


        binding.spTextSize.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setEvent(SettingContract.Event.OnItemListSelectedPosition(position))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }

        initObservers()
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    with(binding) {
                        cbBackground.isChecked = it.darkBackground
//                        spTextSize.setSelection(it.curTextSizeListPosition)
                    }

//                    when (val state = it) {
//                        SettingContract.SettingState.Init -> {
//                            initView()
//                        }
//                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.distinctUntilChanged().collect { effect ->
                    when (effect) {
                        is SettingContract.SideEffect.DeleteKeyword -> deleteSearchKeyword(effect.keyword)
                    }
                }
            }
        }
    }


    private fun initView() {
        setupSpinner()
        setupFilterTextView()
        setupFilterKeywordList()
        setOnClickAddBtn()
    }

    private fun setupSpinner() {
        val textSizes = resources.getStringArray(R.array.text_size_array)
        binding.spTextSize.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, textSizes)
        binding.spTextSize.setSelection(0, false)

//        binding.spTextSize.setSelection(sharedPreferences.getInt(EDDY_LOG_TEXT_SIZE, 10))
//        binding.spTextSize.onItemSelectedListener = object : OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                sharedPreferences.edit().putInt(EDDY_LOG_TEXT_SIZE, position).apply()
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) { }
//
//        }
    }

    private fun setOnClickAddBtn() {
        binding.ivAddFilterKeyword.setOnClickListener {
            val keyword = binding.etInputFilterKeyword.text.toString()
            saveFilterKeywordList(keyword)
            setData()
        }
    }

    private fun setupFilterKeywordList() {
        binding.rvFilterKeywordList.apply {
            setController(controller)

            val models = getFilterKeywordList().map {
                LogKeywordModel(content = it, callback = viewModel)
            }

            controller.setData(models)
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }
    }

    private fun saveFilterKeywordList(keyword: String) {
        if(!arrayListPrefs.contains(keyword)) {
            arrayListPrefs.add(0, keyword)
            stringPrefs = GsonBuilder().create().toJson(
                arrayListPrefs,
                object : TypeToken<ArrayList<String>>() {}.type
            )
            sharedPreferences.edit().apply {
                putString(EDDY_LOG_FILTER_KEYWORD, stringPrefs)
                apply()
            }
        } else {
            Toast.makeText(this, "이미 등록 되어 있는 키워드 입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFilterKeywordList(): List<String> {
        stringPrefs = sharedPreferences.getString(EDDY_LOG_FILTER_KEYWORD, null)

        if(stringPrefs != null && stringPrefs != "[]"){
            arrayListPrefs = GsonBuilder().create().fromJson(
                stringPrefs, object: TypeToken<ArrayList<String>>(){}.type
            )
            return arrayListPrefs
        }
        return emptyList()
    }

    private fun setData() {
        val models = getFilterKeywordList().map {
            LogKeywordModel(content = it, callback = viewModel)
        }
        controller.setData(models)
    }

    private fun deleteSearchKeyword(keyword: String) {

        arrayListPrefs = getFilterKeywordList().toMutableList().also { it.remove(keyword)} as ArrayList<String>

        stringPrefs = GsonBuilder().create().toJson(
            arrayListPrefs,
            object : TypeToken<ArrayList<String>>() {}.type
        )
        sharedPreferences.edit().apply {
            putString(EDDY_LOG_FILTER_KEYWORD, stringPrefs)
            apply()
        }
        setData()
    }

    private fun setupFilterTextView() {
        binding.etInputFilterKeyword.setOnKeyListener { v, keyCode, event ->
            when (keyCode) {
                KeyEvent.KEYCODE_ENTER -> {
                    binding.tvFilterKeyword.text = "ssss"
                    return@setOnKeyListener true
                }
                else -> {
                    return@setOnKeyListener false
                }
            }
        }
    }

    interface Callback {
        val onClickDeleteKeyword: (keyword: String) -> Unit
    }

    override fun onBackPressed() {
        EventBus.getDefault().post(SettingEvent.OnBackPress)
        super.onBackPressed()
    }
}