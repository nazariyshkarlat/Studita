package com.example.studita.domain.entity

data class InterestingData(val interestingNumber: Int, val screens: List<InterestingDataScreen>)

sealed class InterestingDataScreen {

    data class InterestingDataStartScreen(
        val title: String,
        val subtitle: String,
        val difficultyLevel: Int
    ) : InterestingDataScreen()

    data class InterestingDataStepScreen(val title: String?, val subtitle: String?) :
        InterestingDataScreen()

    object InterestingDataSpecificDrumRollScreen : InterestingDataScreen()

    data class InterestingDataExplanationScreen(val textParts: List<String>) :
        InterestingDataScreen()
}

data class InterestingLikeRequestData(val userIdTokenData: UserIdTokenData?, val interestingLikeData: InterestingLikeData)

data class InterestingLikeData(val interestingNumber: Int, val likeIt: Boolean)