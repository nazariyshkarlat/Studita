package com.example.studita.data.entity.exercise

import com.google.gson.JsonArray

object ExercisesJsonArrayMapper{

    fun map(exercisesJsonArray: JsonArray): JsonArray{
        val exercisesArrayFlatten = JsonArray()
        for(jsonElement in exercisesJsonArray){
            if(jsonElement.isJsonArray){
                for(element in jsonElement.asJsonArray)
                    exercisesArrayFlatten.add(element)
            }else
                exercisesArrayFlatten.add(jsonElement)
        }
        return exercisesArrayFlatten
    }
}