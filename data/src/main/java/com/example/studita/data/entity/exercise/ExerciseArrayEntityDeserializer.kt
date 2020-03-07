package com.example.studita.data.entity.exercise

import com.google.gson.*
import java.io.IOException
import java.lang.reflect.Type
import kotlin.reflect.KClass

class ExerciseArrayEntityDeserializer : JsonDeserializer<ExerciseArrayEntity> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?):ExerciseArrayEntity{
        checkNotNull(json)
        checkNotNull(typeOfT)
        checkNotNull(context)

        val jsonObject = json.asJsonObject

        return when (jsonObject.get("type").asString) {
            "exercise" -> {
                ExerciseArrayEntity.ExerciseEntity(jsonObject.get("exercise_number").asInt,
                    when(jsonObject.get("exercise_type").asInt){
                        1 -> context.deserialize<ExerciseInfo.ExerciseType1Info>(jsonObject.get("exercise_info"), ExerciseInfo.ExerciseType1Info::class.java)
                        2 -> context.deserialize<ExerciseInfo.ExerciseType2Info>(jsonObject.get("exercise_info"), ExerciseInfo.ExerciseType2Info::class.java)
                        else -> throw IOException("Unexpected exercise type")
                    }
                )
            }
            "screen" -> {
                ExerciseArrayEntity.ScreenEntity(
                    when (jsonObject.get("screen_type").asInt) {
                        1 -> context.deserialize<ScreenInfo.ScreenType1Info>(
                            jsonObject.get("screen_info"),
                            ScreenInfo.ScreenType1Info::class.java
                        )
                        2 -> context.deserialize<ScreenInfo.ScreenType2Info>(
                            jsonObject.get("screen_info"),
                            ScreenInfo.ScreenType2Info::class.java
                        )
                        else -> throw IOException("Unexpected screen type")
                    }
                )
            }
            else -> {
                throw IOException("Unexpected type")
            }
        }
    }
}