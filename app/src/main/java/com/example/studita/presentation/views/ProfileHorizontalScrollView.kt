package com.example.studita.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import com.example.studita.R
import com.example.studita.utils.ScreenUtils
import com.example.studita.utils.dpToPx
import com.example.studita.utils.makeView
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
                params.marginStart = 8.dpToPx()
            if (index != contentView.childCount - 1)
                params.marginEnd = 8.dpToPx()
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
        contentView!!.post {
            if (XPRatingItem!!.profileCompetitionsItemTitle.lineCount != levelRatingItem!!.profileCompetitionsItemTitle.lineCount) {
                if (XPRatingItem!!.profileCompetitionsItemTitle.lineCount > levelRatingItem!!.profileCompetitionsItemTitle.lineCount) {
                    levelRatingItem!!.profileCompetitionsItemTitle.text =
                        resources.getString(R.string.profile_competitions_item_levels_rating_two_line_title)
                } else {
                    XPRatingItem!!.profileCompetitionsItemTitle.text =
                        resources.getString(R.string.profile_competitions_item_XP_rating_two_line_title)
                }
            }
        }
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