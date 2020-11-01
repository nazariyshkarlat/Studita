package com.example.studita.presentation.fragments.promo_fragments

import com.example.studita.R

class CompetitionsPromoFragment : PromoFragment(){

    override val promoScreens: List<PromoScreen>
        get() = listOf(
            PromoScreen(R.drawable.crossed_swords, R.string.competitions_promo_screen_first_title, R.string.competitions_promo_screen_first_subtitle),
            PromoScreen(R.drawable.handshake, R.string.competitions_promo_screen_second_title, R.string.competitions_promo_screen_second_subtitle),
            PromoScreen(R.drawable.books, R.string.competitions_promo_screen_third_title, R.string.competitions_promo_screen_third_subtitle),
            PromoScreen(R.drawable.bar_chart, R.string.competitions_promo_screen_forth_title, R.string.competitions_promo_screen_forth_subtitle)
        )

}