package com.example.studita.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.updateLayoutParams
import androidx.core.widget.TextViewCompat
import com.example.studita.R
import com.example.studita.presentation.views.press_view.PressTextView
import com.example.studita.utils.ScreenUtils
import com.example.studita.utils.dpToPx

abstract class PromoFragmentScreenView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var screenNumber: Int = 0

    fun formScreenView(screenNumber: Int? = null, @DrawableRes image: Int? = null, @StringRes title: Int, @StringRes subtitle: Int, isEndBottomSheet: Boolean = false) {

        if(!isEndBottomSheet && screenNumber != null)
            this.screenNumber = screenNumber
        else
            this.screenNumber = -1

        if(this.screenNumber > 0) {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            ).apply {
                height =
                    ScreenUtils.getScreenHeight() - ((this@PromoFragmentScreenView.screenNumber - 1) * 24F.dpToPx() + resources.getDimension(
                        R.dimen.toolbarHeight
                    )
                        .toInt() + resources.getDimension(R.dimen.statusBarHeight).toInt())
            }
        }

        addView(
            LinearLayout(context!!).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    this.gravity = Gravity.CENTER
                    marginStart = 32F.dpToPx()
                    marginEnd = 32F.dpToPx()
                    bottomMargin = 48F.dpToPx()/2
                }

                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER

                if(image != null) {
                    addView(
                        ImageView(context!!).apply {
                            setImageResource(image)
                            layoutParams = LinearLayout.LayoutParams(48F.dpToPx(), 48F.dpToPx())
                        }
                    )
                }

                addView(
                    AppCompatTextView(context!!).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            if(image != null) {
                                topMargin = 16F.dpToPx()
                            }
                        }
                        TextViewCompat.setTextAppearance(this, if(!isEndBottomSheet) R.style.Medium24 else R.style.Medium24White)
                        textAlignment = View.TEXT_ALIGNMENT_CENTER
                        text = resources.getString(title)
                    }
                )

                addView(
                    AppCompatTextView(context!!).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            topMargin = 14F.dpToPx()
                        }
                        TextViewCompat.setTextAppearance(this, if(!isEndBottomSheet) R.style.Regular16Secondary else R.style.Regular16White)
                        textAlignment = View.TEXT_ALIGNMENT_CENTER
                        text = resources.getString(subtitle)
                    }
                )
            }
        )

        if(screenNumber == 0){
            addView(
                AppCompatTextView(context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                        bottomMargin  = 24F.dpToPx() + 48F.dpToPx()
                        marginStart = 32F.dpToPx()
                        marginEnd = 32F.dpToPx()
                    }
                    textAlignment = TEXT_ALIGNMENT_CENTER
                    TextViewCompat.setTextAppearance(this, R.style.Regular14Tertiary)
                    text = resources.getString(R.string.swipe_top_to_show)
                }
            )
        }

        if(isEndBottomSheet){
            addView(
                PressTextView(ContextThemeWrapper(context, R.style.WhiteButton), null, 0).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.BOTTOM
                        height = resources.getDimension(R.dimen.buttonHeight).toInt()
                        bottomMargin  = 16F.dpToPx()
                        marginStart = 16F.dpToPx()
                        marginEnd = 16F.dpToPx()
                    }
                    text = resources.getString(R.string.close)

                    setOnClickListener {
                        onCloseButtonClick()
                    }
                }
            )
        }
    }


    fun formBottomSheetPeeckHeader(@StringRes title: Int, isEndBottomSheet: Boolean = false){

        if(screenNumber != 1 && !isEndBottomSheet)
            setBackgroundResource(R.drawable.promo_bottom_sheet_top_layer_background)
        else if(screenNumber == 1){
            setBackgroundResource(R.drawable.promo_bottom_sheet_first_top_layer_background)
        }

        addView(FrameLayout(context).apply {

            id = R.id.promoBottomSheetPeeckView
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP
                height = 48F.dpToPx()
            }

            addView(
                AppCompatTextView(context!!).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.CENTER
                    }
                        TextViewCompat.setTextAppearance(this, if(!isEndBottomSheet) R.style.Medium16Secondary else R.style.Medium16SecondaryDarkTheme)
                    text = resources.getString(title)
                }
            )

            addView(
                ImageView(context!!).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.CENTER_VERTICAL or Gravity.END
                        rightMargin = 8F.dpToPx()
                    }
                    setImageResource(if(!isEndBottomSheet) R.drawable.ic_expand_less else R.drawable.ic_expand_less_secondary_dark_theme)
                    setPadding(12F.dpToPx(), 12F.dpToPx(), 12F.dpToPx(), 12F.dpToPx())
                }
            )

            setOnClickListener {
                onShowLessButtonClick()
            }

            setBackgroundResource(R.drawable.clickable_bottom_sheet_peek_view_background)
        })
    }

    abstract fun onShowLessButtonClick()

    abstract fun onCloseButtonClick()

}