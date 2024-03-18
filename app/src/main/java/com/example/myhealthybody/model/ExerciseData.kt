package com.example.myhealthybody.model

import java.io.Serializable

data class ExerciseData(
    val bodyPart: String = "",
    val equipment: String = "",
    val gifUrl: String = "",
    val id: String = "",
    val name: String = "",
    val target: String = "",
    val secondaryMuscles: List<String> = emptyList(),
    val instructions: List<String> = emptyList(),
    val setItems: List<SetItem> = emptyList(),
    val setTotalWeight: Int = 0
) : Serializable