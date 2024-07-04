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
import com.pl.Maciejbak.R
import com.pl.Maciejbak.exception.ValidationException
import com.pl.Maciejbak.layout.WorkoutExpandableLayout
import com.pl.Maciejbak.layout.WorkoutExpandableTitleLayout
import com.pl.Maciejbak.model.routine.Exercise
import com.pl.Maciejbak.model.routine.ExerciseDraft
import com.pl.Maciejbak.model.workout.WorkoutSeriesDraft
import com.pl.Maciejbak.model.workout.WorkoutExerciseDraft
import com.pl.Maciejbak.model.workout.WorkoutExercise
import com.pl.Maciejbak.model.workout.WorkoutHints
import com.pl.Maciejbak.model.workout.WorkoutSeries
import com.pl.Maciejbak.model.workout.WorkoutSessionSet
import com.google.gson.Gson
import java.io.File
import java.io.FileWriter
import kotlin.math.exp

open class WorkoutExpandableListAdapter(
    private val context: Context,
    private val workout: List<Pair<WorkoutExerciseDraft, List<WorkoutSeriesDraft>>>,
    private var workoutHints: List<WorkoutHints>,
    private var expandableList: ExpandableListView?
) : BaseExpandableListAdapter() {

    constructor(
        context: Context,
        workout: List<Pair<WorkoutExerciseDraft, List<WorkoutSeriesDraft>>>
    ) : this(context, workout, emptyList(), null)

    private val workoutSession = ArrayList<Pair<Int, List<WorkoutSessionSet>>>()


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

        val workoutHint = workoutHints[listPosition]

        workoutExpandableLayout?.setHints(workoutHint)
        workoutExpandableLayout?.setSeries(series, workoutExerciseDraft, expandedListPosition + 1)

        val noteEditText = workoutExpandableLayout?.getNoteEditText()

        noteEditText?.visibility = if (isLastChild) View.VISIBLE else View.GONE


        val repsEditText = workoutExpandableLayout?.getRepsEditText()
        val weightEditText = workoutExpandableLayout?.getWeightEditText()

        updateSessionList(
            repsEditText,
            weightEditText,
            noteEditText,
            listPosition,
            expandedListPosition
        )


        return view
    }

    private fun updateSessionList(
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
                workoutSession[listPosition].second[expandedListPosition].actualReps = reps

            }
        })

        weightEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val weight = workout[listPosition].second[expandedListPosition].load
                workoutSession[listPosition].second[expandedListPosition].load = weight
            }
        })

        noteEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val note = workout[listPosition].first.note
                workoutSession[listPosition].second[expandedListPosition].note = note
            }
        })
    }


    fun saveToFile() {
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


        view?.setOnClickListener {
            if (isExpanded) (parent as ExpandableListView).collapseGroup(listPosition)
            else (parent as ExpandableListView).expandGroup(listPosition, true)
        }

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
                    (i + 1).toLong(),
                    workoutExerciseDraft.exerciseName,
                    workoutExerciseDraft.pause,
                    workoutExerciseDraft.pauseUnit,
                    "0",
                    workoutSeriesDraft.loadUnit,
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

    fun getWorkoutSeries(exerciseIndex: Int): ArrayList<WorkoutSeries> {
        val series = ArrayList<WorkoutSeries>()
        for (i: Int in 0 until getChildrenCount(exerciseIndex)) {
            val workoutExpandableLayout =
                getChildView(exerciseIndex, i, false, null, null) as WorkoutExpandableLayout?
            if (workoutExpandableLayout?.convertHintsToData(workoutHints[exerciseIndex]) == false) {
                expandableList?.expandGroup(exerciseIndex)
                expandableList?.smoothScrollToPosition(exerciseIndex)
                notifyDataSetChanged()
            }
            val workoutSeries: WorkoutSeries? =
                workoutExpandableLayout?.getWorkoutSeriesDraft()?.toWorkoutSeries(i + 1)
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