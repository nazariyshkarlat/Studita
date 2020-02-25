package com.example.studita.data.entity.exercise

import com.google.gson.*
import java.io.IOException
import java.lang.reflect.Type
import kotlin.reflect.KClass

class ExerciseDeserializer : JsonDeserializer<ExerciseEntity> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ExerciseEntity{
        checkNotNull(json)
        checkNotNull(typeOfT)
        checkNotNull(context)

        val jsonObject = json.asJsonObject
        val type = jsonObject.get("exercise_type").asInt
        val infoJson = jsonObject.get("exercise_info")

        return ExerciseEntity(
            jsonObject.get("exercise_number").asInt,
            when (type) {
                1 ->  context.deserialize<ExerciseInfo.ExerciseType1Info>(infoJson, ExerciseInfo.ExerciseType1Info::class.java)
                2 ->  context.deserialize<ExerciseInfo.ExerciseType2Info>(infoJson, ExerciseInfo.ExerciseType2Info::class.java)
                3 ->  context.deserialize<ExerciseInfo.ExerciseType3Info>(infoJson, ExerciseInfo.ExerciseType3Info::class.java)
                4 ->  context.deserialize<ExerciseInfo.ExerciseType4Info>(infoJson, ExerciseInfo.ExerciseType4Info::class.java)
                5 ->  context.deserialize<ExerciseInfo.ExerciseType5Info>(infoJson, ExerciseInfo.ExerciseType5Info::class.java)
                6 ->  context.deserialize<ExerciseInfo.ExerciseType6Info>(infoJson, ExerciseInfo.ExerciseType6Info::class.java)
                7 ->  context.deserialize<ExerciseInfo.ExerciseType7Info>(infoJson, ExerciseInfo.ExerciseType7Info::class.java)
                8 ->  context.deserialize<ExerciseInfo.ExerciseType8Info>(infoJson, ExerciseInfo.ExerciseType8Info::class.java)
                9 ->  context.deserialize<ExerciseInfo.ExerciseType9Info>(infoJson, ExerciseInfo.ExerciseType9Info::class.java)
                else -> throw IOException("Unexpected exercise type")
            }
        )
    }
}