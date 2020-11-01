package com.example.studita.data.entity

import com.example.studita.domain.entity.DuelsInvitesFrom
import com.example.studita.domain.entity.PrivacySettingsData
import com.example.studita.domain.entity.PrivacySettingsRequestData
import com.example.studita.domain.entity.asChar
import com.google.gson.annotations.SerializedName
import java.io.IOException

data class PrivacySettingsRequest(
    @SerializedName("auth_data") val userIdToken: UserIdToken,
    @SerializedName("privacy_settings") val privacySettingsEntity: PrivacySettingsEntity
)

data class PrivacySettingsEntity(
    @SerializedName("duels_invites_from") val duelsInvitesFrom: Char?,
    @SerializedName("show_in_ratings") val showInRatings: Boolean?,
    @SerializedName("profile_is_visible") val profileIsVisible: Boolean?,
    @SerializedName("duels_exceptions") val duelsExceptions: List<String>?
)

fun PrivacySettingsData.toRawEntity() = PrivacySettingsEntity(
    duelsInvitesFrom?.asChar(),
    showInRatings,
    profileIsVisible,
    duelsExceptions
)

fun PrivacySettingsEntity.toBusinessEntity() = PrivacySettingsData(
    duelsInvitesFrom?.toDuelsInvitesFrom(),
    showInRatings,
    profileIsVisible,
    duelsExceptions?.let { ArrayList(it) })

fun PrivacySettingsRequestData.toRawEntity() =
    PrivacySettingsRequest(userIdTokenData.toRawEntity(), privacySettingsEntity.toRawEntity())

fun Char.toDuelsInvitesFrom() = when (this) {
    'f' -> DuelsInvitesFrom.FRIENDS
    'n' -> DuelsInvitesFrom.NOBODY
    'e' -> DuelsInvitesFrom.EXCEPT
    else -> throw IOException("unknown DuelsInvitesFrom char")
}