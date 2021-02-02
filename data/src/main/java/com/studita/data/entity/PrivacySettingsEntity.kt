package com.studita.data.entity

import com.studita.domain.entity.DuelsInvitesFrom
import com.studita.domain.entity.PrivacySettingsData
import com.studita.domain.entity.PrivacySettingsRequestData
import com.studita.domain.entity.asChar
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.IOException

@Serializable
data class PrivacySettingsRequest(
    @SerialName("auth_data") val userIdToken: UserIdToken,
    @SerialName("privacy_settings") val privacySettingsEntity: PrivacySettingsEntity
)

@Serializable
data class PrivacySettingsEntity(
    @SerialName("duels_invites_from") val duelsInvitesFrom: Char?,
    @SerialName("show_in_ratings") val showInRatings: Boolean?,
    @SerialName("profile_is_visible") val profileIsVisible: Boolean?,
    @SerialName("duels_exceptions") val duelsExceptions: List<String>?
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