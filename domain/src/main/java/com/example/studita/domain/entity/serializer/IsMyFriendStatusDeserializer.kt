package com.example.studita.domain.entity.serializer

import com.example.studita.domain.interactor.IsMyFriendStatus
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.IOException
import java.lang.reflect.Type

class IsMyFriendStatusDeserializer : JsonDeserializer<IsMyFriendStatus>{

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): IsMyFriendStatus{
        checkNotNull(json)
        checkNotNull(typeOfT)
        checkNotNull(context)

        val jsonObject = json.asJsonObject

        return when(jsonObject.get("type").asString){
            "is_my_friend"-> IsMyFriendStatus.Success.IsMyFriend(jsonObject.get("friend_id").asInt)
            "is_not_my_friend"-> IsMyFriendStatus.Success.IsNotMyFriend(jsonObject.get("friend_id").asInt)
            "waiting_for_friendship_accept"-> IsMyFriendStatus.Success.WaitingForFriendshipAccept(jsonObject.get("friend_id").asInt)
            "got_my_friendship_request"-> IsMyFriendStatus.Success.GotMyFriendshipRequest(jsonObject.get("friend_id").asInt)
            else -> throw IOException("invalid is my friend status")
        }
    }

}