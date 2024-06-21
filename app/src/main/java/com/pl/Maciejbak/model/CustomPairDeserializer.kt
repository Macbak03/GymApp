package com.pl.Maciejbak.model

import com.pl.Maciejbak.model.workout.WorkoutSessionSet
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class CustomPairDeserializer : JsonDeserializer<List<Pair<Int, List<WorkoutSessionSet>>>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<Pair<Int, List<WorkoutSessionSet>>> {
        val myList = mutableListOf<Pair<Int, List<WorkoutSessionSet>>>()
        val jsonArray = json?.asJsonArray

        jsonArray?.forEach { element ->
            val jsonObj = element.asJsonObject
            val first = jsonObj.get("first").asInt
            val type = object : TypeToken<List<WorkoutSessionSet>>() {}.type
            val workoutSessionSets: List<WorkoutSessionSet> = context?.deserialize(jsonObj.get("second"), type) ?: listOf()
            myList.add(Pair(first, workoutSessionSets))
        }
        return myList
    }
}