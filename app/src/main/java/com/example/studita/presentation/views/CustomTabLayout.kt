package com.example.studita.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.studita.R
import com.example.studita.presentation.utils.*
import com.example.studita.presentation.fragments.UserStatPageFragment


class CustomTabLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), ViewPager.OnPageChangeListener{

    private val padding = 8.dpToPx()
    private val textColor = ColorUtils.getSecondaryColor(context)
    private val typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
    private val textSize = 16F
    private val itemsBackground = ColorUtils.getSelectableItemBackground(context)

    private var viewPager: ViewPager? = null
    private var tabColor: Pair<Int, Int> = ColorUtils.getSecondaryColor(context) to ContextCompat.getColor(context, R.color.blue)
    private var selectedPos = 0
    private var click: Boolean = false

    init {
        gravity = Gravity.CENTER
    }

    fun setItems(items: List<String>){
        for(item in items){

            val textView = TextView(context)

            textView.textSize = textSize
            textView.typeface = typeface
            textView.setTextColor(textColor)

            textView.text = item

            textView.setPadding(padding, padding, padding, padding)

            textView.setBackgroundResource(itemsBackground)

            addView(textView)
        }
    }

    fun syncWithViewPager(viewPager: ViewPager, fragmentManager: FragmentManager){
        this.viewPager = viewPager
        viewPager.adapter = TabsPagerAdapter(fragmentManager)
        viewPager.addOnPageChangeListener(this)
        children.forEachIndexed { index, child ->
            child.setOnClickListener {
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
        if(!click) {
            if (position != childCount - 1) {
                (getChildAt(position + 1) as TextView).setTextColor(
                    ColorUtils.compositeColors(tabColor.first, tabColor.second, positionOffset)
                )
            }
            (getChildAt(position) as TextView).setTextColor(
                ColorUtils.compositeColors(tabColor.first, tabColor.second, 1F - positionOffset)
            )
        }
        if((position == selectedPos) and (positionOffset == 0.0F))
            click = false
    }

    override fun onPageSelected(position: Int) {
        if(click) {
            (getChildAt(selectedPos) as TextView).setTextColor(tabColor.first)
            (getChildAt(position) as TextView).setTextColor(tabColor.second)
        }
        selectedPos = position
    }

    private inner class TabsPagerAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return UserStatPageFragment()
        }

        override fun getCount(): Int {
            return childCount
        }
    }

}