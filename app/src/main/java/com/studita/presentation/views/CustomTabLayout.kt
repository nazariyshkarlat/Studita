package com.studita.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.studita.R
import com.studita.presentation.views.press_view.PressTextView
import com.studita.utils.ColorUtils
import com.studita.utils.ThemeUtils
import com.studita.utils.dpToPx


class CustomTabLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), ViewPager.OnPageChangeListener {

    private val padding = 8F.dpToPx()
    private val textColor = ThemeUtils.getSecondaryColor(context)
    private val typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
    private val textSize = 16F

    private var fragments: List<Fragment>? = null

    private var viewPager: ViewPager? = null
    private var tabColor: Pair<Int, Int> =
        ThemeUtils.getSecondaryColor(context) to ThemeUtils.getAccentColor(context)
    private var selectedPos = 0
    private var click: Boolean = false

    private var items: List<String> = emptyList<String>()

    init {
        gravity = Gravity.CENTER
    }

    fun setItems(items: List<String>) {
        this.items = items
        for (item in items) {

            val textView = PressTextView(context)

            textView.textSize = textSize
            textView.typeface = typeface
            textView.setTextColor(textColor)

            textView.text = item

            textView.setPadding(padding, padding, padding, padding)

            addView(textView)
        }
    }

    fun syncWithViewPager(viewPager: ViewPager, fragmentManager: FragmentManager) {
        this.viewPager = viewPager
        viewPager.adapter = TabsPagerAdapter(fragmentManager)
        viewPager.addOnPageChangeListener(this)
        children.forEachIndexed { index, child ->
            (child as PressTextView).setOnClickListener {
                click = true
                viewPager.currentItem = index
            }
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) {
        if (!click) {
            if (position != childCount - 1) {
                (getChildAt(position + 1) as TextView).setTextColor(
                    ColorUtils.compositeColors(tabColor.first, tabColor.second, positionOffset)
                )
            }
            (getChildAt(position) as TextView).setTextColor(
                ColorUtils.compositeColors(tabColor.first, tabColor.second, 1F - positionOffset)
            )
        }
        if ((position == selectedPos) and (positionOffset == 0.0F))
            click = false
    }

    override fun onPageSelected(position: Int) {
        if (click) {
            (getChildAt(selectedPos) as TextView).setTextColor(tabColor.first)
            (getChildAt(position) as TextView).setTextColor(tabColor.second)
        }
        selectedPos = position
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
            return childCount
        }
    }

}