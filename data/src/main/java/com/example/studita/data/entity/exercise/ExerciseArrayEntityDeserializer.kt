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

        println(jsonObject)

        return when (jsonObject.get("type").asString) {
            "exercise" -> {
                ExerciseArrayEntity.ExerciseEntity(jsonObject.get("exercise_number").asInt,
                    when(jsonObject.get("exercise_type").asInt){
                        1 -> context.deserialize<ExerciseInfo.ExerciseType1Info>(jsonObject.get("exercise_info"), ExerciseInfo.ExerciseType1Info::class.java)
                        2 -> context.deserialize<ExerciseInfo.ExerciseType2Info>(jsonObject.get("exercise_info"), ExerciseInfo.ExerciseType2Info::class.java)
                        3 -> context.deserialize<ExerciseInfo.ExerciseType3Info>(jsonObject.get("exercise_info"), ExerciseInfo.ExerciseType3Info::class.java)
                        4 -> context.deserialize<ExerciseInfo.ExerciseType4Info>(jsonObject.get("exercise_info"), ExerciseInfo.ExerciseType4Info::class.java)
                        5,6 -> context.deserialize<ExerciseInfo.ExerciseType5and6Info>(jsonObject.get("exercise_info"), ExerciseInfo.ExerciseType5and6Info::class.java)
                        7 -> context.deserialize<ExerciseInfo.ExerciseType7Info>(jsonObject.get("exercise_info"), ExerciseInfo.ExerciseType7Info::class.java)
                        8 -> context.deserialize<ExerciseInfo.ExerciseType8Info>(jsonObject.get("exercise_info"), ExerciseInfo.ExerciseType8Info::class.java)
                        9 -> context.deserialize<ExerciseInfo.ExerciseType9Info>(jsonObject.get("exercise_info"), ExerciseInfo.ExerciseType9Info::class.java)
                        10 -> context.deserialize<ExerciseInfo.ExerciseType10Info>(jsonObject.get("exercise_info"), ExerciseInfo.ExerciseType10Info::class.java)
                        11 -> context.deserialize<ExerciseInfo.ExerciseType11Info>(jsonObject.get("exercise_info"), ExerciseInfo.ExerciseType11Info::class.java)
                        else -> throw IOException("Unexpected exercise type")
                    }, if(!jsonObject.has("answer")) null else jsonObject.get("answer").asString
                )
            }
            "screen" -> {
                ExerciseArrayEntity.ScreenEntity(if(!jsonObject.has("exercise_number")) null else jsonObject.get("exercise_number").asInt,
                    when (jsonObject.get("screen_type").asInt) {
                        1 -> context.deserialize<ScreenInfo.ScreenType1Info>(
                            jsonObject.get("screen_info"),
                            ScreenInfo.ScreenType1Info::class.java
                        )
                        2 -> context.deserialize<ScreenInfo.ScreenType2Info>(
                            jsonObject.get("screen_info"),
                            ScreenInfo.ScreenType2Info::class.java
                        )
                        3 -> context.deserialize<ScreenInfo.ScreenType3Info>(
                            jsonObject.get("screen_info"),
                            ScreenInfo.ScreenType3Info::class.java
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