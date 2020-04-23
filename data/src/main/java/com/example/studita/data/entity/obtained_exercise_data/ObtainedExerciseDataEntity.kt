package com.example.studita.data.entity.obtained_exercise_data

import com.google.gson.annotations.SerializedName

data class ObtainedExerciseDataEntity(@SerializedName("new_levels_count")val newLevelsCount: Int,
                                      @SerializedName("training")val training: Boolean,
                                      @SerializedName("new_level_XP")val newLevelXP: Int,
                                      @SerializedName("obtained_XP")val obtainedXP: Int,
                                      @SerializedName("obtained_time")val obtainedTime: Long,
                                      @SerializedName("chapter_number")val chapterNumber: Int)