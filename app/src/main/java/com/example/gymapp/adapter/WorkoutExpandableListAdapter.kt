package com.example.gymapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.example.gymapp.R
import com.example.gymapp.layout.WorkoutExpandableLayout
import com.example.gymapp.layout.WorkoutExpandableTitleLayout
import com.example.gymapp.model.routine.ExerciseDraft
import com.example.gymapp.model.workout.WorkoutSeriesDraft
import com.example.gymapp.model.workout.WorkoutExerciseDraft
import com.example.gymapp.model.workout.WorkoutExercise
import com.example.gymapp.model.workout.WorkoutSeries
import com.example.gymapp.model.workout.WorkoutSessionSet
import com.google.gson.Gson
import java.io.File
import java.io.FileWriter

class WorkoutExpandableListAdapter(
    private val context: Context,
    private val workout: List<Pair<WorkoutExerciseDraft, List<WorkoutSeriesDraft>>>

) : BaseExpandableListAdapter() {

    private val workoutSession = ArrayList<Pair<Int, List<WorkoutSessionSet>>>()

    init {
        initWorkoutSession()
    }

    private fun initWorkoutSession(){
        workout.forEachIndexed { index, pair ->
            val series = pair.second
            val workoutSessionSets = ArrayList<WorkoutSessionSet>()
            series.forEachIndexed { seriesIndex, workoutSeriesDraft ->
                val actualReps = workoutSeriesDraft.actualReps
                val load = workoutSeriesDraft.load
                val note = pair.first.note
                if (actualReps != null && load != null && note != null) {
                    val workoutSessionSet =
                        WorkoutSessionSet(index, seriesIndex, actualReps, load, note)
                    workoutSessionSets.add(workoutSessionSet)
                }
            }
            workoutSession.add(Pair(index, workoutSessionSets))
        }
    }

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        return workout[listPosition].second[expandedListPosition]
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    @SuppressLint("InflateParams")
    override fun getChildView(
        listPosition: Int,
        expandedListPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        var view = convertView
        if (view == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.workout_expandable_layout_helper, null)
        }
        val series = getChild(listPosition, expandedListPosition) as WorkoutSeriesDraft
        val workoutExerciseDraft = getGroup(listPosition) as WorkoutExerciseDraft
        val workoutExpandableLayout = view as WorkoutExpandableLayout?
        workoutExpandableLayout?.setSeries(series, workoutExerciseDraft, expandedListPosition + 1)
        val noteEditText = workoutExpandableLayout?.getNoteEditText()
        if (!isLastChild) {
            noteEditText?.visibility = View.GONE
        } else {
            noteEditText?.visibility = View.VISIBLE
        }

        workoutExpandableLayout?.setWorkoutSessionSetIds(listPosition, expandedListPosition)

        val repsEditText = workoutExpandableLayout?.getRepsEditText()
        val weightEditText = workoutExpandableLayout?.getWeightEditText()

        repsEditText?.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                workoutSession[listPosition].second[expandedListPosition].actualReps = s.toString()
            }
        })

        weightEditText?.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                workoutSession[listPosition].second[expandedListPosition].load = s.toString()
            }
        })

        noteEditText?.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                workoutSession[listPosition].second[expandedListPosition].note = s.toString()
            }
        })


        return view
    }

    private fun retrieveWorkoutSession(){

    }

    fun saveToFile(){
        val gson = Gson()
        val jsonData = gson.toJson(workoutSession)

        try {
            val file = File(context.filesDir, "workout_session.json")
            val writer = FileWriter(file, false)
            writer.write(jsonData)
            writer.close()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return workout[listPosition].first.series?.toInt() ?: 0
    }

    override fun getGroup(listPosition: Int): Any {
        return workout[listPosition].first
    }

    override fun getGroupCount(): Int {
        return workout.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    @SuppressLint("InflateParams")
    override fun getGroupView(
        listPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        var view = convertView
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.workout_expandable_title_layout_helper, null)
        }
        val exercise = getGroup(listPosition) as WorkoutExerciseDraft
        val workoutExpandableTitleLayout = view as WorkoutExpandableTitleLayout?
        workoutExpandableTitleLayout?.setExerciseAttributes(exercise)
        return view
    }

    fun getWorkoutGroup(): ArrayList<WorkoutExercise> {
        val workout = ArrayList<WorkoutExercise>()
        for (i: Int in 0 until groupCount) {
            val workoutExpandableTitleLayout =
                getGroupView(i, true, null, null) as WorkoutExpandableTitleLayout?
            val workoutExerciseDraft = workoutExpandableTitleLayout?.getWorkoutExerciseDraft()
            val workoutExpandableLayout =
                getChildView(
                    i,
                    getChildrenCount(i) - 1,
                    true,
                    null,
                    null
                ) as WorkoutExpandableLayout?
            val workoutSeriesDraft = workoutExpandableLayout?.getWorkoutSeriesDraft()
            val note = workoutExpandableLayout?.getNote()
            if (workoutExerciseDraft != null && workoutSeriesDraft != null) {
                val exerciseDraft = ExerciseDraft(
                    (i+1).toLong(),
                    workoutExerciseDraft.exerciseName,
                    workoutExerciseDraft.pause,
                    workoutExerciseDraft.pauseUnit,
                    workoutSeriesDraft.load,
                    workoutSeriesDraft.loadUnit,
                    workoutExerciseDraft.series,
                    workoutExerciseDraft.reps,
                    workoutExerciseDraft.rpe,
                    workoutExerciseDraft.pace,
                    true
                )
                val exercise = exerciseDraft.toExercise()
                workout.add(WorkoutExercise(exercise, i + 1, note))
            }
        }
        return workout
    }

    fun getWorkoutSeries(exerciseIndex: Int): ArrayList<WorkoutSeries> {
        val series = ArrayList<WorkoutSeries>()
        for (i: Int in 0 until getChildrenCount(exerciseIndex)) {
            val workoutExpandableLayout =
                getChildView(exerciseIndex, i, false, null, null) as WorkoutExpandableLayout?
            val workoutSeries =
                workoutExpandableLayout?.getWorkoutSeriesDraft()?.toWorkoutSeries(i + 1)
            if (workoutSeries != null) {
                series.add(workoutSeries)
            }
        }
        return series
    }

    fun getWorkoutSession(): ArrayList<WorkoutSessionSet> {
        val session = ArrayList<WorkoutSessionSet>()
        for (groupPosition in 0 until groupCount) {
            for (childPosition in 0 until getChildrenCount(groupPosition)) {
                (getChildView(groupPosition, childPosition, false, null, null) as? WorkoutExpandableLayout)
                    ?.getWorkoutSessionSet()
                    ?.apply {
                        groupId = groupPosition
                        childId = childPosition
                    }
                    ?.also { workoutSession ->
                        session.add(workoutSession)
                    }
            }
        }
        return session
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }

}