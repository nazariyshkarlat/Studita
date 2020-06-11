package com.example.studita.domain.entity


data class PrivacySettingsData(val duelsInvitesFrom: DuelsInvitesFrom? = null,
                                 val showInRatings: Boolean? = null,
                                 val profileIsVisible: Boolean? = null,
                                 val duelsExceptions: List<String>? = null)

enum class DuelsInvitesFrom{
    FRIENDS,
    NOBODY,
    EXCEPT
}

data class PrivacySettingsRequestData(val userIdToken: UserIdTokenData,
                                  val privacySettingsEntity: PrivacySettingsData)

fun DuelsInvitesFrom.asChar() = when(this){
    DuelsInvitesFrom.FRIENDS -> 'f'
    DuelsInvitesFrom.NOBODY -> 'n'
    DuelsInvitesFrom.EXCEPT -> 'e'
}