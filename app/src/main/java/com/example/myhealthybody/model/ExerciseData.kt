package com.example.myhealthybody.model

import java.io.Serializable

data class ExerciseData(
    val bodyPart: String,
    val equipment: String,
    val gifUrl: String,
    val id: String,
    val name: String,
    val target: String,
    val secondaryMuscles: List<String>,
    val instructions: List<String>,
    val setItems: List<SetItem>
) : Serializable