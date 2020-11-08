package com.studita.data.entity.exercise

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.io.IOException
import java.lang.reflect.Type

class ExerciseResponseDescriptionDeserializer :
    JsonDeserializer<ExerciseResponseDescriptionEntity> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ExerciseResponseDescriptionEntity {
        checkNotNull(json)
        checkNotNull(typeOfT)
        checkNotNull(context)

        val jsonObject = json.asJsonObject

        return when (jsonObject.get("description_type").asString) {
            "text" -> {
                ExerciseResponseDescriptionEntity(
                    ExerciseResponseDescriptionContent.DescriptionContentString(
                        jsonObject.get("description_content").asString
                    )
                )
            }
            "shape" -> {
                ExerciseResponseDescriptionEntity(
                    ExerciseResponseDescriptionContent.DescriptionContentArray(
                        jsonObject.get("description_content").asJsonArray.map { it.asString })
                )
            }
            else -> {
                throw IOException("Unexpected type")
            }
        }
    }
}