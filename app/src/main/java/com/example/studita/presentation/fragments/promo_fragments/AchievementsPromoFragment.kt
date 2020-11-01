package com.example.studita.presentation.fragments.promo_fragments

import com.example.studita.R

class AchievementsPromoFragment : PromoFragment(){

    override val promoScreens: List<PromoScreen>
        get() = listOf(
            PromoScreen(R.drawable.ballot_box_with_check, R.string.achievements_promo_screen_first_title, R.string.achievements_promo_screen_first_subtitle),
            PromoScreen(R.drawable.slightly_smiling_face, R.string.achievements_promo_screen_second_title, R.string.achievements_promo_screen_second_subtitle),
            PromoScreen(R.drawable.wrapped_present, R.string.achievements_promo_screen_third_title, R.string.achievements_promo_screen_third_subtitle),
            PromoScreen(R.drawable.rocket, R.string.achievements_promo_screen_forth_title, R.string.achievements_promo_screen_forth_subtitle)
        )

}