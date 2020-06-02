package com.example.studita.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.core.view.children
import com.example.studita.R
import com.example.studita.utils.ScreenUtils
import com.example.studita.utils.dpToPx
import com.example.studita.utils.makeView
import kotlinx.android.synthetic.main.profile_competitions_item.view.*
import kotlin.math.abs


class ProfileHorizontalScrollView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr), View.OnTouchListener {

    private var levelRatingItem: View? = null
    private var XPRatingItem: View? = null
    private val SWIPE_MIN_DISTANCE = 5
    private val SWIPE_THRESHOLD_VELOCITY = 300
    private var mActiveFeature = 0
    private val itemsSize = 2
    private var featureWidth = 0
    private val gestureDetector = GestureDetector(MyGestureDetector())

    init {
        this.addView(getContentView())
        this.setOnTouchListener(this)
    }

    private fun getContentView(): ViewGroup{
        val contentView = LinearLayout(context)
        contentView.orientation = LinearLayout.HORIZONTAL
        val ratingItem = getLevelRatingItem()
        val XPItem = getXPRatingItem()
        contentView.addView(ratingItem)
        contentView.addView(XPItem)
        setParams(contentView)
        return contentView
    }

    private fun setParams(contentView: ViewGroup){
        contentView.children.forEachIndexed {index,view->
            val params = view.layoutParams as MarginLayoutParams
            if(index != 0)
                params.marginStart = 16.dpToPx()
            params.width = (ScreenUtils.getScreenWidth()*0.75).toInt()
            featureWidth = params.width
            view.layoutParams = params
        }
    }

    private fun getLevelRatingItem(): View{
        levelRatingItem = makeView(R.layout.profile_competitions_item)
        levelRatingItem!!.profileCompetitionsItemTitle.text = resources.getString(R.string.profile_competitions_item_levels_rating_title)
        return levelRatingItem!!
    }

    private fun getXPRatingItem(): View{
        XPRatingItem = makeView(R.layout.profile_competitions_item)
        XPRatingItem!!.profileCompetitionsItemTitle.text = resources.getString(R.string.profile_competitions_item_XP_rating_title)
        return XPRatingItem!!
    }

    fun setLevelRatingSubtitle(text: String){
        levelRatingItem?.profileCompetitionsItemRatingSubtitle?.text = text

    }

    fun setXPRatingSubtitle(text: String){
        XPRatingItem?.profileCompetitionsItemRatingSubtitle?.text = text
    }

    fun setLevelSecondarySubtitle(text: String){
        levelRatingItem?.profileCompetitionsItemSecondarySubtitle?.visibility = View.VISIBLE
        levelRatingItem?.profileCompetitionsItemSecondarySubtitle?.text = text
    }

    fun setXPSecondarySubtitle(text: String){
        XPRatingItem?.profileCompetitionsItemSecondarySubtitle?.visibility = View.VISIBLE
        XPRatingItem?.profileCompetitionsItemSecondarySubtitle?.text = text
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            val scrollX = scrollX
            mActiveFeature = (scrollX + featureWidth / 2) / featureWidth
            val scrollTo = mActiveFeature * featureWidth
            smoothScrollTo(scrollTo, 0)
            true
        } else {
            false
        }
    }

    private inner class MyGestureDetector : SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            try {
                if (e1.x - e2.x > SWIPE_MIN_DISTANCE && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mActiveFeature = if (mActiveFeature < itemsSize - 1) mActiveFeature + 1 else itemsSize - 1
                    smoothScrollTo(mActiveFeature * featureWidth, 0)
                    return true
                } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mActiveFeature = if (mActiveFeature > 0) mActiveFeature - 1 else 0
                    smoothScrollTo(mActiveFeature * featureWidth, 0)
                    return true
                }
            } catch (e: Exception) {
                Log.e("Fling", "There was an error processing the Fling event:" + e.message)
            }
            return false
        }
    }
}