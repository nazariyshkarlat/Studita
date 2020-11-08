package com.studita.domain.entity


data class PrivacySettingsData(
    var duelsInvitesFrom: DuelsInvitesFrom? = null,
    var showInRatings: Boolean? = null,
    var profileIsVisible: Boolean? = null,
    val duelsExceptions: ArrayList<String>? = null
)

enum class DuelsInvitesFrom {
    FRIENDS,
    NOBODY,
    EXCEPT
}

data class PrivacySettingsRequestData(
    val userIdTokenData: UserIdTokenData,
    val privacySettingsEntity: PrivacySettingsData
)

fun DuelsInvitesFrom.asChar() = when (this) {
    DuelsInvitesFrom.FRIENDS -> 'f'
    DuelsInvitesFrom.NOBODY -> 'n'
    DuelsInvitesFrom.EXCEPT -> 'e'
}