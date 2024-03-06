package com.example.gymapp.model

import com.example.gymapp.model.workout.WorkoutSessionSet
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class WorkoutSessionSetDeserializer : JsonDeserializer<WorkoutSessionSet> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): WorkoutSessionSet {
        val jsonObject = json.asJsonObject

        val groupId = jsonObject.get("groupId").asJsonPrimitive.asInt
        val childId = jsonObject.get("childId").asJsonPrimitive.asInt
        val actualReps = jsonObject.get("actualReps").asString
        val load = jsonObject.get("load").asString
        val note = jsonObject.get("note").asString

        return WorkoutSessionSet(groupId, childId, actualReps, load, note)
    }
}