package com.example.studita.data.entity

import com.google.gson.annotations.SerializedName

data class PrivacySettingsRequest(@SerializedName("auth_data")val userIdToken: UserIdToken,
                                  @SerializedName("privacy_settings")val privacySettingsEntity: PrivacySettingsEntity)

data class PrivacySettingsEntity(@SerializedName("duels_invites_from")val duelsInvitesFrom: Char?,
                                 @SerializedName("show_in_ratings")val showInRatings: Boolean?,
                                 @SerializedName("profile_is_visible")val profileIsVisible: Boolean?,
                                 @SerializedName("duels_exceptions")val duelsExceptions: List<String>?)