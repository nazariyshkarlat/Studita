package com.example.studita.data.entity.exercise

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.io.IOException
import java.lang.reflect.Type

class ExerciseArrayEntityDeserializer : JsonDeserializer<ExerciseArrayEntity> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ExerciseArrayEntity {
        checkNotNull(json)
        checkNotNull(typeOfT)
        checkNotNull(context)

        val jsonObject = json.asJsonObject

        return when (jsonObject.get("type").asString) {
            "exercise" -> {
                ExerciseArrayEntity.ExerciseEntity(
                    jsonObject.get("exercise_number").asInt,
                    when (jsonObject.get("exercise_type").asInt) {
                        1 -> context.deserialize<ExerciseInfo.ExerciseType1Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType1Info::class.java
                        )
                        2 -> context.deserialize<ExerciseInfo.ExerciseType2Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType2Info::class.java
                        )
                        3 -> context.deserialize<ExerciseInfo.ExerciseType3Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType3Info::class.java
                        )
                        4 -> context.deserialize<ExerciseInfo.ExerciseType4Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType4Info::class.java
                        )
                        5 -> context.deserialize<ExerciseInfo.ExerciseType5Info>(
                            jsonObject.get(
                                "exercise_info"
                            ), ExerciseInfo.ExerciseType5Info::class.java
                        )
                        6 -> context.deserialize<ExerciseInfo.ExerciseType6Info>(
                            jsonObject.get(
                                "exercise_info"
                            ), ExerciseInfo.ExerciseType6Info::class.java
                        )
                        7 -> context.deserialize<ExerciseInfo.ExerciseType7Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType7Info::class.java
                        )
                        8 -> context.deserialize<ExerciseInfo.ExerciseType8Info>(
                            jsonObject.get(
                                "exercise_info"
                            ), ExerciseInfo.ExerciseType8Info::class.java
                        )
                        9 -> context.deserialize<ExerciseInfo.ExerciseType9Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType9Info::class.java
                        )
                        10 -> context.deserialize<ExerciseInfo.ExerciseType10Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType10Info::class.java
                        )
                        11 -> context.deserialize<ExerciseInfo.ExerciseType11Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType11Info::class.java
                        )
                        12 -> context.deserialize<ExerciseInfo.ExerciseType12Info>(
                            jsonObject.get(
                                "exercise_info"
                            ), ExerciseInfo.ExerciseType12Info::class.java
                        )
                        13 -> context.deserialize<ExerciseInfo.ExerciseType13Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType13Info::class.java
                        )
                        14 -> context.deserialize<ExerciseInfo.ExerciseType14Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType14Info::class.java
                        )
                        15 -> context.deserialize<ExerciseInfo.ExerciseType15Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType15Info::class.java
                        )
                        16 -> context.deserialize<ExerciseInfo.ExerciseType16Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType16Info::class.java
                        )
                        17 -> context.deserialize<ExerciseInfo.ExerciseType17Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType17Info::class.java
                        )
                        18 -> context.deserialize<ExerciseInfo.ExerciseType18Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType18Info::class.java
                        )
                        19 -> context.deserialize<ExerciseInfo.ExerciseType19Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType19Info::class.java
                        )
                        20 -> context.deserialize<ExerciseInfo.ExerciseType20Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType20Info::class.java
                        )
                        21 -> context.deserialize<ExerciseInfo.ExerciseType21Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType21Info::class.java
                        )
                        22 -> context.deserialize<ExerciseInfo.ExerciseType22Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType22Info::class.java
                        )
                        23 -> context.deserialize<ExerciseInfo.ExerciseType23Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType23Info::class.java
                        )
                        24 -> context.deserialize<ExerciseInfo.ExerciseType24Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType24Info::class.java
                        )
                        25 -> context.deserialize<ExerciseInfo.ExerciseType25Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType25Info::class.java
                        )
                        26 -> context.deserialize<ExerciseInfo.ExerciseType26Info>(
                            jsonObject.get("exercise_info"),
                            ExerciseInfo.ExerciseType26Info::class.java
                        )
                        else -> throw IOException("Unexpected exercise type")
                    },
                    if (!jsonObject.has("answer")) null else jsonObject.get("answer").asString,
                    if (!jsonObject.has("is_bonus")) false else jsonObject.get("is_bonus").asBoolean
                )
            }
            "screen" -> {
                ExerciseArrayEntity.ScreenEntity(
                    if (!jsonObject.has("exercise_number")) null else jsonObject.get("exercise_number").asInt,
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
                        4 -> context.deserialize<ScreenInfo.ScreenType4Info>(
                            jsonObject.get("screen_info"),
                            ScreenInfo.ScreenType4Info::class.java
                        )
                        5 -> context.deserialize<ScreenInfo.ScreenType5Info>(
                            jsonObject.get("screen_info"),
                            ScreenInfo.ScreenType5Info::class.java
                        )
                        else -> throw IOException("Unexpected screen type")
                    }
                )
            }
            "explanation" -> {
                ExerciseArrayEntity.ExplanationEntity(
                    if (!jsonObject.has("exercise_number")) null else jsonObject.get("exercise_number").asInt,
                    context.deserialize<ExplanationInfo>(
                        jsonObject.get("explanation_info"),
                        ExplanationInfo::class.java
                ))
            }
            else -> {
                throw IOException("Unexpected type")
            }
        }
    }
}