package com.studita.data.entity.level

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.IOException
import java.lang.reflect.Type

class LevelsDeserializer : JsonDeserializer<LevelEntity> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LevelEntity {
        checkNotNull(json)
        checkNotNull(typeOfT)
        checkNotNull(context)

        val jsonObject = json.asJsonObject

        val children = jsonObject.get("children").asJsonArray

        return LevelEntity(jsonObject.get("level_number").asInt, ArrayList(children.map {
            it as JsonObject
            when (it.get("type").asString) {
                "chapter" -> context.deserialize<LevelChildEntity.LevelChapterEntity>(
                    it,
                    LevelChildEntity.LevelChapterEntity::class.java
                )
                "interesting" -> context.deserialize<LevelChildEntity.LevelInterestingEntity>(
                    it,
                    LevelChildEntity.LevelInterestingEntity::class.java
                )
                "subscribe" -> context.deserialize<LevelChildEntity.LevelSubscribeEntity>(
                    it,
                    LevelChildEntity.LevelSubscribeEntity::class.java
                )
                else -> {
                    throw IOException("Unexpected type")
                }
            }
        }))
    }
}