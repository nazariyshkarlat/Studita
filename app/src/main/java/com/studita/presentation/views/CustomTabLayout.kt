package com.studita.presentation.views

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.InsetDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.OneShotPreDrawListener
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.studita.R
import com.studita.presentation.views.press_view.PressTextView
import com.studita.utils.ThemeUtils
import com.studita.utils.dp


class CustomTabLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ViewPager.OnPageChangeListener {

    companion object{
        private const val CHANGE_TAB_SELECTED_STATE_OFFSET_PERCENT = 0.25F
        private const val TAB_SELECT_DURATION = 150L
        private const val TAB_UN_SELECT_DURATION = 250L
        private const val TAB_SELECT_DRAGGING_DURATION = 200L
        private const val TAB_UN_SELECT_DRAGGING_DURATION = 200L
    }

    private val itemsPadding = 6F.dp
    private val contentMargin = 2F.dp
    private val textColor = ThemeUtils.getPrimaryColor(context)
    private val typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
    private val textSize = 14F
    private var tabIsSelected = true
    private var currentItemIsAnimatingToUnselectedState = false
    private var currentItemIsAnimatingToSelectedState = false

    private var tabColor: Pair<Int, Int> =
        ThemeUtils.getPrimaryColor(context) to ContextCompat.getColor(context, R.color.white)

    private val contentLayout: LinearLayout by lazy {
        LinearLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                rightMargin = contentMargin
                leftMargin = contentMargin
                topMargin = contentMargin
                bottomMargin = contentMargin
            }
            orientation = LinearLayout.HORIZONTAL
        }
    }

    private val indicatorView: View by lazy {
        View(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_VERTICAL
                rightMargin = contentMargin
                leftMargin = contentMargin
            }
            background = ContextCompat.getDrawable(context, R.drawable.tab_layout_indicator)
        }
    }

    private var fragments: List<Fragment>? = null

    var viewPager: ViewPager? = null
    private var selectedPos = 0
    private var isClick: Boolean = false
    private var viewPagerState = ViewPager.SCROLL_STATE_IDLE


    private var items: List<String> = emptyList<String>()

    init {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        addView(contentLayout)

        background = InsetDrawable(
            ContextCompat.getDrawable(context, R.drawable.blocks_rectangle),
            paddingLeft,
            paddingTop,
            paddingRight,
            paddingBottom
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val newHeight = contentLayout.measuredHeight
        val newWidth =  if (items.isEmpty()) 0 else contentLayout.measuredWidth / items.size
        if(indicatorView.measuredHeight != newHeight &&
                indicatorView.measuredWidth !=  newWidth) {
            indicatorView.updateLayoutParams<FrameLayout.LayoutParams> {
                height = newHeight
                width = newWidth
            }
            measure(widthMeasureSpec, heightMeasureSpec)
        }
    }


    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        background = InsetDrawable(
            ContextCompat.getDrawable(context, R.drawable.blocks_rectangle),
            left,
            top,
            right,
            bottom
        )
    }

    fun setItems(items: List<String>) {
        this.items = items
        items.forEachIndexed { index, item ->

            val textView = PressTextView(context).apply {

                textSize = this@CustomTabLayout.textSize
               typeface = this@CustomTabLayout.typeface
                setTextColor(this@CustomTabLayout.textColor)
                textAlignment = TEXT_ALIGNMENT_CENTER

                text = item

                setPadding(0, itemsPadding, 0, itemsPadding)

                setOnClickListener {
                    isClick = true
                    viewPager?.currentItem = index
                }

                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1.0F
                }
            }

            contentLayout.addView(textView)
        }
    }

    fun syncWithViewPager(viewPager: ViewPager, fragmentManager: FragmentManager) {
        if(this.viewPager == null) {
            this.viewPager = viewPager
            viewPager.adapter = TabsPagerAdapter(fragmentManager)
            viewPager.addOnPageChangeListener(this)
            this.addView(indicatorView, 0)
            selectedPos = viewPager.currentItem
            onPageSelected(selectedPos)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        viewPagerState = state
    }

    override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) {
        val offsetFromLowToHigh = if(selectedPos - position == 0) positionOffset else 1F-positionOffset
        if(!isClick && viewPagerState != ViewPager.SCROLL_STATE_IDLE){
            if(tabNeedsToBeUnselected(offsetFromLowToHigh)){
                animateTabUnSelect(selectedPos, true)
            }else if(tabNeedsToBeSelected(offsetFromLowToHigh)){
                animateTabSelect(selectedPos, true)
            }
        }
        indicatorView.translationX = ((position+positionOffset)*((contentLayout.measuredWidth)/items.size.toFloat()))

        if ((position == selectedPos) && (positionOffset == 0.0F))
            isClick = false
    }

    private fun tabNeedsToBeUnselected(offsetFromLowToHigh: Float) =
        (offsetFromLowToHigh > CHANGE_TAB_SELECTED_STATE_OFFSET_PERCENT &&
            (contentLayout.getChildAt(selectedPos) as TextView).currentTextColor != tabColor.first &&
            !currentItemIsAnimatingToUnselectedState &&
            viewPagerState == ViewPager.SCROLL_STATE_DRAGGING)

    private fun tabNeedsToBeSelected(offsetFromLowToHigh: Float) =
        (offsetFromLowToHigh < CHANGE_TAB_SELECTED_STATE_OFFSET_PERCENT &&
            (contentLayout.getChildAt(selectedPos) as TextView).currentTextColor != tabColor.second &&
            !currentItemIsAnimatingToSelectedState)


    override fun onPageSelected(position: Int) {
        tabIsSelected = true
        if(isClick){
            (contentLayout.getChildAt(selectedPos) as TextView).setTextColor(
                tabColor.first
            )
        }
        if(selectedPos != position) {
            animateTabSelect(position, false)
            animateTabUnSelect(selectedPos, false)
            selectedPos = position
        }else{
            (contentLayout.getChildAt(position) as TextView).setTextColor(
                tabColor.second
            )
        }
    }

    private fun animateTabUnSelect(lastPos: Int, isDragging: Boolean){
        currentItemIsAnimatingToUnselectedState = true
        currentItemIsAnimatingToSelectedState = false
        val prevItemColorAnimation =
            ValueAnimator.ofObject(
                ArgbEvaluator(),
                (contentLayout.getChildAt(lastPos) as TextView).currentTextColor,
                tabColor.first
            )
        prevItemColorAnimation.duration = if(isDragging) TAB_UN_SELECT_DRAGGING_DURATION else TAB_UN_SELECT_DURATION
        prevItemColorAnimation.addUpdateListener { animator ->
            (contentLayout.getChildAt(lastPos) as TextView).setTextColor(
                animator.animatedValue as Int
            )
            currentItemIsAnimatingToUnselectedState = animator.animatedFraction != 1F
        }
        prevItemColorAnimation.start()
    }

    private fun animateTabSelect(position: Int, isDragging: Boolean){
        currentItemIsAnimatingToSelectedState = true
        currentItemIsAnimatingToUnselectedState = false
        val newItemColorAnimation =
            ValueAnimator.ofObject(
                ArgbEvaluator(),
                (contentLayout.getChildAt(position) as TextView).currentTextColor,
                tabColor.second
            )
        newItemColorAnimation.duration = if(isDragging) TAB_SELECT_DRAGGING_DURATION else TAB_SELECT_DURATION
        newItemColorAnimation.addUpdateListener { animator ->
            (contentLayout.getChildAt(position) as TextView).setTextColor(
                animator.animatedValue as Int
            )
            currentItemIsAnimatingToSelectedState = animator.animatedFraction != 1F
        }
        newItemColorAnimation.start()
    }

    fun setFragments(fragments: List<Fragment>) {
        this.fragments = fragments
    }

    fun getItemsSize() = this.items.size

    private inner class TabsPagerAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return fragments!![position]
        }

        override fun getCount(): Int {
            return contentLayout.childCount
        }
    }

}