package com.studita.presentation.fragments.promo_fragments

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.studita.presentation.fragments.bottom_sheets.PromoBottomSheetEndFragment
import com.studita.presentation.fragments.bottom_sheets.PromoBottomSheetFragment
import com.studita.presentation.views.PromoFragmentScreenView
import com.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.bottomdrawer.BottomDrawerFragment

abstract class PromoFragment : Fragment(), PromoBottomSheetFragment.BottomSheetExpandCallback {

    abstract val promoScreens: List<PromoScreen>

    lateinit var parentLayout: FrameLayout

    private var currentBottomSheet: BottomDrawerFragment? = null

    private var screenNumber = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parentLayout = formParentLayout()
        return parentLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        screenNumber = savedInstanceState?.getInt("CURRENT_SCREEN_NUMBER", 1) ?: 1

        (view as ViewGroup).addView(object : PromoFragmentScreenView(context!!){
            override fun onShowLessButtonClick() {}

            override fun onCloseButtonClick() {}

        }.apply {
            formScreenView(
                0,
                promoScreens[0].image,
                promoScreens[0].title,
                promoScreens[0].subtitle
            )
        })

        if(savedInstanceState == null) {
            PromoBottomSheetFragment
                .getInstance(
                    1,
                    promoScreens[1].image,
                    promoScreens[1].title,
                    promoScreens[1].subtitle
                )
                .show(childFragmentManager, null)
        }
    }



        private fun formParentLayout() = FrameLayout(context!!).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
    }

    override fun onExpand() {

        screenNumber++
        currentBottomSheet = if(screenNumber < promoScreens.size) {
            PromoBottomSheetFragment
                .getInstance(
                    screenNumber,
                    promoScreens[screenNumber].image,
                    promoScreens[screenNumber].title,
                    promoScreens[screenNumber].subtitle
                )
        }else{
            PromoBottomSheetEndFragment()
        }
        currentBottomSheet?.show(childFragmentManager, null)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("CURRENT_SCREEN_NUMBER", screenNumber)
        super.onSaveInstanceState(outState)
    }

}
data class PromoScreen(
    @DrawableRes val image: Int,
    @StringRes val title: Int,
    @StringRes val subtitle: Int
)