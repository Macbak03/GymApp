package com.pl.Maciejbak.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.EditText
import android.widget.ExpandableListView
import com.google.gson.Gson
import com.pl.Maciejbak.R
import com.pl.Maciejbak.exception.ValidationException
import com.pl.Maciejbak.layout.NoPlanWorkoutExpandableLayout
import com.pl.Maciejbak.layout.NoPlanWorkoutExpandableTitleLayout
import com.pl.Maciejbak.model.routine.Exercise
import com.pl.Maciejbak.model.routine.ExerciseDraft
import com.pl.Maciejbak.model.routine.IntensityIndex
import com.pl.Maciejbak.model.routine.TimeUnit
import com.pl.Maciejbak.model.routine.WeightUnit
import com.pl.Maciejbak.model.workout.NoPlanWorkoutSessionExercise
import com.pl.Maciejbak.model.workout.WorkoutExercise
import com.pl.Maciejbak.model.workout.WorkoutExerciseDraft
import com.pl.Maciejbak.model.workout.WorkoutSeries
import com.pl.Maciejbak.model.workout.WorkoutSeriesDraft
import com.pl.Maciejbak.model.workout.WorkoutSessionSet
import java.io.File
import java.io.FileWriter


class NoPlanWorkoutExpandableListAdapter(
    private val context: Context,
    private val workout: MutableList<Pair<WorkoutExerciseDraft, MutableList<WorkoutSeriesDraft>>>,
    private val weightUnit: WeightUnit,
) : BaseExpandableListAdapter() {

    private val workoutSession =
        ArrayList<Pair<Int, NoPlanWorkoutSessionExercise>>()

    init {
        initWorkoutSession()
    }

    private fun initWorkoutSession() {
        workout.forEachIndexed { index, pair ->
            val series = pair.second
            val exercise = pair.first
            val workoutSessionSets = ArrayList<WorkoutSessionSet>()
            series.forEachIndexed { seriesIndex, workoutSeriesDraft ->
                val actualReps = workoutSeriesDraft.actualReps
                val load = workoutSeriesDraft.load
                val note = exercise.note
                val isChecked = workoutSeriesDraft.isChecked
                if (actualReps != null && load != null && note != null) {
                    val workoutSessionSet =
                        WorkoutSessionSet(
                            index,
                            seriesIndex,
                            actualReps,
                            load,
                            note,
                            isChecked
                        )
                    workoutSessionSets.add(workoutSessionSet)
                }
            }
            val workoutSessionExercise =
                NoPlanWorkoutSessionExercise(index, exercise.exerciseName, workoutSessionSets)
            workoutSession.add(Pair(index, workoutSessionExercise))
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
            view = inflater.inflate(R.layout.no_plan_workout_expandable_layout_helper, null)
        }
        val series = getChild(listPosition, expandedListPosition) as WorkoutSeriesDraft
        val workoutExerciseDraft = getGroup(listPosition) as WorkoutExerciseDraft
        val noPlanWorkoutExpandableLayout = view as NoPlanWorkoutExpandableLayout?

        noPlanWorkoutExpandableLayout?.setSeries(
            series,
            workoutExerciseDraft,
            expandedListPosition + 1
        )

        val noteEditText = noPlanWorkoutExpandableLayout?.getNoteEditText()

        noteEditText?.visibility = if (isLastChild) View.VISIBLE else View.GONE


        val repsEditText = noPlanWorkoutExpandableLayout?.getRepsEditText()
        val weightEditText = noPlanWorkoutExpandableLayout?.getWeightEditText()
        updateSessionSetList(
            repsEditText,
            weightEditText,
            noteEditText,
            listPosition,
            expandedListPosition
        )

        val addSetButton = noPlanWorkoutExpandableLayout?.getAddSetButton()
        addSetButton?.visibility = if (isLastChild) View.VISIBLE else View.GONE
        addSetButton?.setOnClickListener {
            addSet(listPosition)
        }

        return view
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return workout[listPosition].second.size
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
            view = inflater.inflate(R.layout.no_plan_workout_expandable_title_layout_helper, null)
        }
        val exercise = getGroup(listPosition) as WorkoutExerciseDraft
        val noPlanWorkoutExpandableTitleLayout = view as NoPlanWorkoutExpandableTitleLayout?
        noPlanWorkoutExpandableTitleLayout?.setExerciseAttributes(exercise)

        val exerciseNameEditText = noPlanWorkoutExpandableTitleLayout?.getExerciseNameEditText()
        updateSessionExerciseList(exerciseNameEditText, listPosition)


        val addExerciseButton = noPlanWorkoutExpandableTitleLayout?.getAddExerciseButton()
        addExerciseButton?.visibility =
            if (listPosition < workout.size - 1) View.GONE else View.VISIBLE
        addExerciseButton?.setOnClickListener {
            addExercise()
        }

        view?.setOnClickListener {
            if (isExpanded) (parent as ExpandableListView).collapseGroup(listPosition)
            else (parent as ExpandableListView).expandGroup(listPosition, true)
        }

        return view
    }

    private fun addExercise() {
        val seriesList = ArrayList<WorkoutSeriesDraft>()
        seriesList.add(WorkoutSeriesDraft("", "", weightUnit, false))
        val defaultExerciseName = ""
        val defaultWorkoutValue = "0"
        val defaultPaceValue = "0000"
        workout.add(
            Pair(
                WorkoutExerciseDraft(
                    defaultExerciseName,
                    defaultWorkoutValue,
                    TimeUnit.s,
                    defaultWorkoutValue,
                    defaultWorkoutValue,
                    defaultWorkoutValue,
                    IntensityIndex.RPE,
                    defaultPaceValue,
                    "",
                    false
                ), seriesList
            )
        )
        val groupId = workout.size - 1
        val workoutExerciseSets = ArrayList<WorkoutSessionSet>()
        workoutExerciseSets.add(WorkoutSessionSet(groupId, 0, "", "", "", false))
        val noPlanWorkoutExercise =
            NoPlanWorkoutSessionExercise(groupId, defaultExerciseName, workoutExerciseSets)
        workoutSession.add(Pair(groupId, noPlanWorkoutExercise))
        notifyDataSetChanged()
    }

    private fun addSet(listPosition: Int) {
        val set = WorkoutSeriesDraft("", "", weightUnit, false)
        workout[listPosition].second.add(set)
        val childId = workout[listPosition].second.size - 1
        val sessionSet = WorkoutSessionSet(listPosition, childId, "", "", workout[listPosition].first.note, false)
        workoutSession[listPosition].second.workoutSessionExerciseSets.add(sessionSet)
        notifyDataSetChanged()
    }

    private fun updateSessionExerciseList(
        exerciseNameEditText: EditText?,
        listPosition: Int,
    ) {
        exerciseNameEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val exerciseName = workout[listPosition].first.exerciseName
                workoutSession[listPosition].second.exerciseName = exerciseName
            }

        })
    }

    private fun updateSessionSetList(
        repsEditText: EditText?,
        weightEditText: EditText?,
        noteEditText: EditText?,
        listPosition: Int,
        expandedListPosition: Int
    ) {
        repsEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val reps = workout[listPosition].second[expandedListPosition].actualReps
                workoutSession[listPosition].second.workoutSessionExerciseSets[expandedListPosition].actualReps =
                    reps
            }

        })

        weightEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val load = workout[listPosition].second[expandedListPosition].load
                workoutSession[listPosition].second.workoutSessionExerciseSets[expandedListPosition].load =
                    load
            }

        })

        noteEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val note = workout[listPosition].first.note
                workoutSession[listPosition].second.workoutSessionExerciseSets[expandedListPosition].note =
                    note
            }

        })
    }

    fun saveNoPlanSessionToFile() {
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

    fun getNoPlanWorkoutGroup(): ArrayList<WorkoutExercise> {
        val workout = ArrayList<WorkoutExercise>()
        for (i: Int in 0 until groupCount) {
            val noPlanWorkoutExpandableTitleLayout =
                getGroupView(i, true, null, null) as NoPlanWorkoutExpandableTitleLayout?
            val workoutExerciseDraft = noPlanWorkoutExpandableTitleLayout?.getWorkoutExerciseDraft()
            val workoutExpandableLayout =
                getChildView(
                    i,
                    getChildrenCount(i) - 1,
                    true,
                    null,
                    null
                ) as NoPlanWorkoutExpandableLayout?
            val noPlanWorkoutSeriesDraft = workoutExpandableLayout?.getWorkoutSeriesDraft()
            val note = workoutExpandableLayout?.getNote()
            if (workoutExerciseDraft != null && noPlanWorkoutSeriesDraft != null) {
                val exerciseDraft = ExerciseDraft(
                    (i + 1).toLong(),
                    workoutExerciseDraft.exerciseName,
                    workoutExerciseDraft.pause,
                    workoutExerciseDraft.pauseUnit,
                    "0",
                    noPlanWorkoutSeriesDraft.loadUnit,
                    workoutExerciseDraft.series,
                    workoutExerciseDraft.reps,
                    workoutExerciseDraft.intensity,
                    workoutExerciseDraft.intensityIndex,
                    workoutExerciseDraft.pace,
                    true
                )
                val exercise: Exercise? = try {
                    exerciseDraft.toExercise()
                } catch (exception: ValidationException) {
                    null
                }
                if (exercise != null) {
                    workout.add(WorkoutExercise(exercise, i + 1, note))
                }
            }
        }
        return workout
    }

    fun getNoPlanWorkoutSeries(exerciseIndex: Int): ArrayList<WorkoutSeries> {
        val series = ArrayList<WorkoutSeries>()
        for (i: Int in 0 until getChildrenCount(exerciseIndex)) {
            val noPlanWorkoutExpandableLayout =
                getChildView(exerciseIndex, i, false, null, null) as NoPlanWorkoutExpandableLayout?
            val workoutSeries: WorkoutSeries? =
                noPlanWorkoutExpandableLayout?.getWorkoutSeriesDraft()?.toWorkoutSeries(i + 1)
            if (workoutSeries != null) {
                series.add(workoutSeries)
            }
        }
        return series
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}