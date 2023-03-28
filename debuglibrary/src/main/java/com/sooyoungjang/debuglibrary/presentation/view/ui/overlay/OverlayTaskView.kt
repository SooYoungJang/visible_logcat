package com.sooyoungjang.debuglibrary.presentation.view.ui.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleService
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.example.debuglibrary.R
import com.sooyoungjang.debuglibrary.presentation.view.model.LogUiModel
import com.sooyoungjang.debuglibrary.presentation.view.ui.overlay.epoxy.LogController
import com.sooyoungjang.debuglibrary.presentation.view.ui.search.SearchActivity
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.SettingActivity
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.SettingContract
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

internal class OverlayTaskView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val callback: OverlayTaskCallback
) : FrameLayout(context, attrs, defStyleAttr) {

    private val rootView: RelativeLayout by lazy { inflate.inflate(R.layout.view_in_overlay_popup, null) as RelativeLayout }
    private val windowManager: WindowManager by lazy { context.getSystemService(LifecycleService.WINDOW_SERVICE) as WindowManager }
    private val rootViewParams: WindowManager.LayoutParams by lazy {
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
    }

    private var touchX = 0
    private var touchY = 0
    private var viewX = 0
    private var viewY = 0
    private var isExpandView = false

    private val screenRatio = 3
    private val screenFullRatio = 1.5

    private val inflate: LayoutInflater by lazy { context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater }
    private val ivMove: ImageView by lazy { rootView.findViewById(R.id.iv_move) }
    private val ivSetting: ImageView by lazy { rootView.findViewById(R.id.iv_setting) }
    private val tvLog: TextView by lazy { rootView.findViewById(R.id.tv_log) }
    private val ivClose: ImageView by lazy { rootView.findViewById(R.id.iv_close) }
    private val cbZoom: CheckBox by lazy { rootView.findViewById(R.id.cb_zoom) }
    private val spLog: Spinner by lazy { rootView.findViewById(R.id.sp_log) }
    private val ivTrashLog: ImageView by lazy { rootView.findViewById(R.id.iv_trash_log) }
    private val ivSearch: ImageView by lazy { rootView.findViewById(R.id.iv_search) }
    private val llSearchTool: LinearLayout by lazy { rootView.findViewById(R.id.ly_search_tool) }
    private val ivUpBtn: ImageView by lazy { rootView.findViewById(R.id.iv_up) }
    private val ivDownBtn: ImageView by lazy { rootView.findViewById(R.id.iv_down) }
    private val logController: LogController by lazy { LogController() }
    private val tvSearchKeyword: TextView by lazy { rootView.findViewById(R.id.tv_search_keyword) }
    private val rvLog: EpoxyRecyclerView by lazy { rootView.findViewById(R.id.rv_logs) }

    init {
        windowManager.addView(rootView, rootViewParams)

        EventBus.getDefault().register(this@OverlayTaskView)
        setRv()
        setClickListener()
    }

    fun onSearchKeyword(keyword: String) {
        try {
            callback.onClickSearch.invoke(logController.currentData, keyword)
        } catch (e: Exception) {
            makeToast("Not found. Search Log")
        }
    }

    fun onDestroyView() {
        windowManager.removeView(rootView)
    }

    fun setState(state: OverlayTaskContract.State) {
        ivSetting.isVisible = state.setting
        isExpandView = state.expandView
        ivClose.isVisible = state.close
        spLog.isVisible = state.filterKeyword
        cbZoom.isVisible = state.zoom
        cbZoom.isChecked = state.zoomChecked
        ivTrashLog.isVisible = state.trash
        ivSearch.isVisible = state.searching
        llSearchTool.isVisible = state.searchLayout
        ivMove.isVisible = state.move
        tvLog.isVisible = state.keywordTitle

        if (state.filterKeywordList.isNotEmpty()) spLog.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, state.filterKeywordList)
        if (state.log) {
            rvLog.apply {
                updateLayoutParams {
                    width = Resources.getSystem().displayMetrics.widthPixels
                    height = ((Resources.getSystem().displayMetrics.heightPixels / screenRatio))
                }
                setBackgroundColor(ContextCompat.getColor(context, state.backgroundColor))
                isVisible = true
            }
        } else {
            rvLog.apply {
                isVisible = false
                removeAllViewsInLayout()
            }
        }

        spLog.setSelection(state.keywordSelectedPosition)

        if (state.expandView) {
            tvLog.text = state.filterKeywordTitle
            val moveLayoutParams = ivMove.layoutParams as RelativeLayout.LayoutParams
            moveLayoutParams.removeRule(RelativeLayout.RIGHT_OF)
            moveLayoutParams.addRule(RelativeLayout.LEFT_OF, ivClose.id)
            ivMove.layoutParams = moveLayoutParams
            rootViewParams.width = WindowManager.LayoutParams.MATCH_PARENT
            callback.onCollectLog.invoke("normal")
        } else {
            tvLog.text = state.logTitle
            val moveLayoutParams = ivMove.layoutParams as RelativeLayout.LayoutParams
            moveLayoutParams.removeRule(RelativeLayout.LEFT_OF)
            moveLayoutParams.addRule(RelativeLayout.RIGHT_OF, tvLog.id)
            ivMove.layoutParams = moveLayoutParams
            rootViewParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        }

        windowManager.updateViewLayout(rootView, rootViewParams)
    }

    fun setEffect(effect: OverlayTaskContract.SideEffect) {
        when (effect) {
            is OverlayTaskContract.SideEffect.FetchLogs -> fetchLogs(effect.logs)
            is OverlayTaskContract.SideEffect.ScrollPosition -> scrollPosition(effect.position)
            is OverlayTaskContract.SideEffect.SearchLog -> setSearchMode(effect.keyword, effect.position)
            is OverlayTaskContract.SideEffect.Error.NotFoundLog -> makeToast(effect.message)
            is OverlayTaskContract.SideEffect.BackPressed -> backPressedEvent(effect.filterKeywordList, effect.backgroundColor)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickListener() {
        ivTrashLog.setOnClickListener {
            callback.onClickDelete.invoke()
            logController.setData(listOf())
        }

        ivSearch.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        ivUpBtn.setOnClickListener {
            val position = getScrollPosition()
            val keyword = tvSearchKeyword.text.toString()
            callback.onClickPageUp.invoke(logController.currentData, keyword, position)
        }

        ivDownBtn.setOnClickListener {
            val position = getScrollPosition()
            val keyword = tvSearchKeyword.text.toString()
            callback.onClickPageDown.invoke(logController.currentData, keyword, position)
        }

        ivSetting.setOnClickListener {
            onDestroyView()

            val intent = Intent(context, SettingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        ivClose.setOnClickListener {
            callback.onClickClose.invoke()
        }

        ivClose.setOnLongClickListener {
            EventBus.getDefault().unregister(this@OverlayTaskView)
            callback.onLongClickCloseService.invoke()
            true
        }

        tvLog.setOnClickListener {
            if (isExpandView.not()) callback.onClickOpen.invoke()
        }

        cbZoom.setOnClickListener {
            if (cbZoom.isChecked) {
                rvLog.updateLayoutParams {
                    width = Resources.getSystem().displayMetrics.widthPixels
                    height = ((Resources.getSystem().displayMetrics.heightPixels / screenFullRatio).toInt())
                }
            } else {
                rvLog.updateLayoutParams {
                    width = Resources.getSystem().displayMetrics.widthPixels
                    height = ((Resources.getSystem().displayMetrics.heightPixels / screenRatio))
                }
            }
        }

        spLog.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                callback.onClickTagItem.invoke(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        ivMove.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchX = event.rawX.toInt()
                    touchY = event.rawY.toInt()
                    viewX = rootViewParams.x
                    viewY = rootViewParams.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val x = (event.rawX - touchX).toInt()
                    val y = (event.rawY - touchY).toInt()
                    rootViewParams.x = viewX + x
                    rootViewParams.y = viewY + y
                    windowManager.updateViewLayout(rootView, rootViewParams)
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun onCreateView() {
        windowManager.addView(rootView, rootViewParams)
    }

    private fun setRv() {
        rvLog.apply {
            setController(logController)
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }
    }

    private fun scrollPosition(position: Int) {
        try {
            val layoutManager = rvLog.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(position, 0)
        } catch (e: Exception) {
            makeToast("The end has been reached.")
        }
    }

    private fun getScrollPosition(): Int {
        val layoutManager = rvLog.layoutManager as LinearLayoutManager
        return layoutManager.findFirstVisibleItemPosition()
    }

    private fun backPressedEvent(keywords: List<String>, color: Int) {
        if (keywords.isNotEmpty()) { spLog.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, keywords) }

        rvLog.setBackgroundColor(ContextCompat.getColor(context, color))
    }

    private fun fetchLogs(log: List<LogUiModel>) {
        logController.setData(log)
    }

    private fun setSearchMode(keyword: String, position: Int) {
        llSearchTool.isVisible = true
        tvSearchKeyword.text = keyword
        rvLog.smoothScrollToPosition(position)
    }

    private fun makeToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    @Subscribe
    fun settingEventHandler(event: SettingContract.SideEffect) {
        when (event) {
            SettingContract.SideEffect.OnBackPressed -> {
                onCreateView()
                callback.onClickBackPressed.invoke()
            }
        }
    }
}