package com.studita.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import com.studita.R
import com.studita.utils.ScreenUtils
import com.studita.utils.disableAllItems
import com.studita.utils.dp
import com.studita.utils.makeView
import kotlinx.android.synthetic.main.profile_competitions_item.view.*


class ProfileHorizontalScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SnapHorizontalScrollView(context, attrs, defStyleAttr), View.OnTouchListener {

    private var levelRatingItem: View? = null
    private var XPRatingItem: View? = null
    private var contentView: ViewGroup? = null

    init {
        addView(getContentView())
        makeItemsSameHeight()
        disableItems()
    }

    private fun getContentView(): ViewGroup {
        return if (contentView == null) {
            val contentView = LinearLayout(context)
            this.contentView = contentView
            contentView.orientation = LinearLayout.HORIZONTAL
            val levelItem = getLevelRatingItem()
            val XPItem = getXPRatingItem()
            contentView.addView(levelItem)
            contentView.addView(XPItem)
            setParams(contentView)
            contentView
        } else
            this.contentView!!
    }

    private fun setParams(contentView: ViewGroup) {
        contentView.children.forEachIndexed { index, view ->
            val params = view.layoutParams as MarginLayoutParams
            if (index != 0)
                params.marginStart = 8F.dp
            if (index != contentView.childCount - 1)
                params.marginEnd = 8F.dp
            params.width = (ScreenUtils.getScreenWidth() * 0.75).toInt()
            view.layoutParams = params
        }
    }

    private fun getLevelRatingItem(): View {
        levelRatingItem = makeView(R.layout.profile_competitions_item)
        levelRatingItem!!.profileCompetitionsItemTitle.text =
            resources.getString(R.string.profile_competitions_item_levels_rating_title)
        return levelRatingItem!!
    }

    private fun getXPRatingItem(): View {
        XPRatingItem = makeView(R.layout.profile_competitions_item)
        XPRatingItem!!.profileCompetitionsItemTitle.text =
            resources.getString(R.string.profile_competitions_item_XP_rating_title)
        return XPRatingItem!!
    }

    private fun makeItemsSameHeight() {
        levelRatingItem!!.profileCompetitionsItemTitle.viewTreeObserver
            .addOnGlobalLayoutListener {
                if (levelRatingItem!!.profileCompetitionsItemTitle.lineCount > 0) {
                    if (XPRatingItem!!.profileCompetitionsItemTitle.measuredHeight != levelRatingItem!!.profileCompetitionsItemTitle.measuredHeight) {
                        if (XPRatingItem!!.profileCompetitionsItemTitle.measuredHeight > levelRatingItem!!.profileCompetitionsItemTitle.measuredHeight) {
                            levelRatingItem!!.profileCompetitionsItemTitle.text =
                                resources.getString(R.string.profile_competitions_item_levels_rating_two_line_title)
                        } else {
                            XPRatingItem!!.profileCompetitionsItemTitle.text =
                                resources.getString(R.string.profile_competitions_item_XP_rating_two_line_title)
                        }
                    }
                }
            }
    }

    private fun disableItems(){
        levelRatingItem?.disableAllItems()
        XPRatingItem?.disableAllItems()
    }

    fun setLevelRatingSubtitle(text: String) {
        levelRatingItem?.profileCompetitionsItemRatingSubtitle?.text = text

    }

    fun setXPRatingSubtitle(text: String) {
        XPRatingItem?.profileCompetitionsItemRatingSubtitle?.text = text
    }

    fun setLevelSecondarySubtitle(text: String) {
        levelRatingItem?.profileCompetitionsItemSecondarySubtitle?.visibility = View.VISIBLE
        levelRatingItem?.profileCompetitionsItemSecondarySubtitle?.text = text
    }

    fun setXPSecondarySubtitle(text: String) {
        XPRatingItem?.profileCompetitionsItemSecondarySubtitle?.visibility = View.VISIBLE
        XPRatingItem?.profileCompetitionsItemSecondarySubtitle?.text = text
    }
}