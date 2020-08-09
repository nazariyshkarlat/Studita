package com.example.studita.domain.entity.serializer

import com.example.studita.domain.interactor.IsMyFriendStatus
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class IsMyFriendStatusSerializer : JsonSerializer<IsMyFriendStatus.Success> {
    override fun serialize(
        src: IsMyFriendStatus.Success?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {

        checkNotNull(src)
        checkNotNull(typeOfSrc)
        checkNotNull(context)

        val jsonObject = JsonObject()

        jsonObject.addProperty("friend_id", src.userId)

        when (src) {
            is IsMyFriendStatus.Success.IsMyFriend -> jsonObject.addProperty(
                "type",
                "is_my_friend"
            )
            is IsMyFriendStatus.Success.IsNotMyFriend -> jsonObject.addProperty(
                "type",
                "is_not_my_friend"
            )
            is IsMyFriendStatus.Success.WaitingForFriendshipAccept -> jsonObject.addProperty(
                "type",
                "waiting_for_friendship_accept"
            )
            is IsMyFriendStatus.Success.GotMyFriendshipRequest -> jsonObject.addProperty(
                "type",
                "got_my_friendship_request"
            )
        }

        return jsonObject
    }


}