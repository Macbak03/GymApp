package com.pl.Maciejbak.model.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.pl.Maciejbak.model.workout.NoPlanWorkoutSessionExercise
import java.lang.reflect.Type

class NoPlanWorkoutSessionSetListDeserializer :
    JsonDeserializer<List<Pair<Int, NoPlanWorkoutSessionExercise>>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<Pair<Int, NoPlanWorkoutSessionExercise>> {
        val myList = mutableListOf<Pair<Int, NoPlanWorkoutSessionExercise>>()
        val jsonArray = json?.asJsonArray

        jsonArray?.forEach { element ->
            val jsonObj = element.asJsonObject
            val first = jsonObj.get("first").asInt
            val type = object : TypeToken<NoPlanWorkoutSessionExercise>() {}.type
            val noPlanWorkoutSessionExercise: NoPlanWorkoutSessionExercise? = context?.deserialize(jsonObj.get("second"), type)
            if (noPlanWorkoutSessionExercise != null) {
                myList.add(Pair(first, noPlanWorkoutSessionExercise))
            }
        }
        return myList
    }
}