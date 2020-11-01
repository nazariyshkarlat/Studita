package com.example.studita.presentation.fragments.promo_fragments

import com.example.studita.R

class TrainingsPromoFragment : PromoFragment(){

    override val promoScreens: List<PromoScreen>
        get() = listOf(
            PromoScreen(R.drawable.flexed_biceps, R.string.trainings_promo_screen_first_title, R.string.trainings_promo_screen_first_subtitle),
            PromoScreen(R.drawable.artist_palette, R.string.trainings_promo_screen_second_title, R.string.trainings_promo_screen_second_subtitle),
            PromoScreen(R.drawable.chart_with_upwards_trend, R.string.trainings_promo_screen_third_title, R.string.trainings_promo_screen_third_subtitle),
            PromoScreen(R.drawable.smiling_face_with_open_mouth_and_smiling_eyes, R.string.trainings_promo_screen_forth_title, R.string.trainings_promo_screen_forth_subtitle)
            )

}