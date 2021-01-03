package com.studita.domain.entity

import android.graphics.Color
import com.studita.domain.date.DateTimeFormat
import com.studita.domain.interactor.IsMyFriendStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

@Serializable
data class UserDataData(
    val userId: Int?,
    var userName: String?,
    var name: String?,
    var bio: String?,
    var userPublicId: String?,
    var avatarLink: String?,
    var currentLevel: Int,
    var currentLevelXP: Int,
    var streakDays: Int,
    var isSubscribed: Boolean,
    val completedParts: ArrayList<Int>,
    @Serializable(with = DateSerializer::class)
    var streakDatetime: Date,
    var todayCompletedExercises: Int = 0,
    var notificationsAreChecked: Boolean
)

object DateSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeString(DateTimeFormat().format(value))
    }

    override fun deserialize(decoder: Decoder): Date {
        return DateTimeFormat().parse(decoder.decodeString())!!
    }
}

fun UserDataData.toUserData(isMyFriendStatus: IsMyFriendStatus.Success) =
    UserData(userId!!, userName!!, avatarLink, isMyFriendStatus)