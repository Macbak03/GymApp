package com.example.gymapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.Toast
import com.example.gymapp.databinding.ActivityCreateRoutineBinding
import com.example.gymapp.model.Routine

class CreateRoutineActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateRoutineBinding
    private lateinit var expandableListView: ExpandableListView
    private lateinit var routineExpandableListAdapter: RoutineExpandableListAdapter
    private lateinit var routines: List<Routine>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        expandableListView = binding.ExpandableListViewRoutineItems
        routines = ArrayList<Routine>()
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