package com.example.gymapp

import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gymapp.databinding.ActivityCreateRoutineBinding
import com.example.gymapp.model.ExactReps
import com.example.gymapp.model.Routine
import com.example.gymapp.model.Rpe
import com.example.gymapp.model.Weight
import com.example.gymapp.model.WeightUnit
import kotlin.time.Duration.Companion.minutes


class CreateRoutineActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateRoutineBinding
    private lateinit var expandableListView: ExpandableListView
    private lateinit var routineExpandableListAdapter: RoutineExpandableListAdapter
    private val routines: MutableList<Routine> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        expandableListView = binding.ExpandableListViewRoutineItems
        val routine = Routine("exercise", 2.minutes, Weight(100f, WeightUnit.kg),
            10, ExactReps(3), Rpe(8))
        routines.add(routine)
        routines.add(routine)
        routineExpandableListAdapter = RoutineExpandableListAdapter(this, routines)
        expandableListView.setAdapter(routineExpandableListAdapter)


        expandableListView.setOnGroupExpandListener { groupPosition ->
            Toast.makeText(
                applicationContext,
                "${routines[groupPosition]} List Expanded.",
                Toast.LENGTH_SHORT
            ).show()
        }

        expandableListView.setOnGroupCollapseListener { groupPosition ->
            Toast.makeText(
                applicationContext,
                "${routines[groupPosition]} List Collapsed.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

}