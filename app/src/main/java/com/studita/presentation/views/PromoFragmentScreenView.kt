package com.studita.presentation.views

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
import androidx.core.widget.TextViewCompat
import com.studita.R
import com.studita.presentation.views.press_view.PressTextView
import com.studita.utils.ScreenUtils
import com.studita.utils.dp

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
                    ScreenUtils.getScreenHeight() - ((this@PromoFragmentScreenView.screenNumber - 1) * 24F.dp + resources.getDimension(
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
                    marginStart = 32F.dp
                    marginEnd = 32F.dp
                    bottomMargin = 48F.dp/2
                }

                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER

                if(image != null) {
                    addView(
                        ImageView(context!!).apply {
                            setImageResource(image)
                            layoutParams = LinearLayout.LayoutParams(48F.dp, 48F.dp)
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
                                topMargin = 16F.dp
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
                            topMargin = 14F.dp
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
                        bottomMargin  = 24F.dp + 48F.dp
                        marginStart = 32F.dp
                        marginEnd = 32F.dp
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
                        bottomMargin  = 16F.dp
                        marginStart = 16F.dp
                        marginEnd = 16F.dp
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
                height = 48F.dp
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
                        rightMargin = 8F.dp
                    }
                    setImageResource(if(!isEndBottomSheet) R.drawable.ic_expand_less else R.drawable.ic_expand_less_secondary_dark_theme)
                    setPadding(12F.dp, 12F.dp, 12F.dp, 12F.dp)
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