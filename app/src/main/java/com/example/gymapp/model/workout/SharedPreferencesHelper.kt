package com.example.gymapp.model.workout

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("WorkoutSharedPreferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveList(key: String, list: MutableList<Pair<WorkoutExerciseDraft, List<WorkoutSeriesDraft>>>) {
        val json = gson.toJson(list)
        sharedPreferences.edit().putString(key, json).apply()
    }

    fun getList(key: String): MutableList<Pair<WorkoutExerciseDraft, List<WorkoutSeriesDraft>>> {
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<Pair<WorkoutExerciseDraft, List<WorkoutSeriesDraft>>>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }
}