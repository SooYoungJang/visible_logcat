package com.sooyoungjang.debuglibrary.presentation.view.ui.setting

import SettingScreenRoute
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.debuglibrary.R
import com.example.debuglibrary.databinding.ActivitySettingBinding
import com.sooyoungjang.debuglibrary.di.AppContainer
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.epoxy.LogKeywordController
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.viewmodel.SettingViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus


internal class SettingActivity : AppCompatActivity() {

    private var _binding: ActivitySettingBinding? = null
    private val binding get() = _binding!!

    private val appContainer: AppContainer by lazy { AppContainer(this) }
    private val controller: LogKeywordController by lazy { LogKeywordController() }
    private val viewModel: SettingViewModel by lazy { SettingViewModel(appContainer.sharedPreferencesUtil) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingBinding.inflate(layoutInflater)
        supportActionBar?.title = getString(R.string.setting)
//        setContentView(binding.root)

        setContent {
            MaterialTheme {
                Surface {
                    SettingScreenRoute(viewModel = viewModel)
//                    Text("이거 되는거 맞냐잉??")
//                    Text("lknlknkl")
                }
            }
        }



        setupBackPressed()
        setupSpinner()
        setupFilterKeywordList()

        binding.cbBackground.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setEvent(SettingContract.Event.OnDarkBackgroundClick(isChecked))
        }

        binding.spTextSize.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setEvent(SettingContract.Event.OnItemListSelectedPosition(position))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.ivAddFilterKeyword.setOnClickListener {
            val keyword = binding.etInputFilterKeyword.text.toString()
            viewModel.setEvent(SettingContract.Event.OnAddFilterKeyword(keyword))
        }

        initObservers()
    }

    private fun initObservers() {
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.uiState.collect {
//                    with(binding) {
//                        cbBackground.isChecked = it.darkBackground
//                        spTextSize.setSelection(it.curTextSizeListPosition, false)
//                        controller.setData(it.filterKeywordModels)
//                    }
//                }
//            }
//        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.distinctUntilChanged().collect { effect ->
                    when (effect) {
                        SettingContract.SideEffect.OnBackPressed -> {
                            EventBus.getDefault().post(SettingEvent.OnBackPress)
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun setupSpinner() {
        val textSizes = resources.getStringArray(R.array.text_size_array)
        binding.spTextSize.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, textSizes)
    }

    private fun setupFilterKeywordList() {
        binding.rvFilterKeywordList.apply {
            setController(controller)
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }
    }

    private fun setupBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.setEvent(SettingContract.Event.OnBackPressed)
            }
        })
    }

    interface Callback {
        val onClickDeleteKeyword: (keyword: String) -> Unit
    }

}