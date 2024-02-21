package com.example.myhealthybody.model

data class SelectSetData(val selectData: ExerciseData, val editData: EditData)
data class EditData(var editTextLines: MutableList<EditTextLine> = mutableListOf())
data class EditTextLine(var kg: String = "", var count: String = "")