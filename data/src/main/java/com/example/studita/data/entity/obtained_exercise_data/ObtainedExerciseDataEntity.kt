package com.example.studita.data.entity.obtained_exercise_data

import com.example.studita.data.entity.UserTokenId
import com.google.gson.annotations.SerializedName

data class ObtainedExerciseDataEntity(@SerializedName("update_level")val updateLevel: Boolean,
                                      @SerializedName("training")val training: Boolean,
                                      @SerializedName("new_level_XP")val newLevelXP: Int,
                                      @SerializedName("obtained_XP")val obtainedXP: Int,
                                      @SerializedName("obtained_time")val obtainedTime: Long,
                                      @SerializedName("chapter_number")val chapterNumber: Int)