package com.example.gymapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.adapter.TrainingPlansRecyclerViewAdapter
import com.example.gymapp.databinding.ActivityTrainingPlansBinding
import com.example.gymapp.exception.ValidationException
import com.example.gymapp.model.trainingPlans.TrainingPlan
import com.example.gymapp.persistence.PlansDataBaseHelper

class TrainingPlansActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrainingPlansBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var trainingPlansRecyclerViewAdapter: TrainingPlansRecyclerViewAdapter

    private val dataBase = PlansDataBaseHelper(this, null)
    private var trainingPlansNames: MutableList<TrainingPlan> = ArrayList()

    companion object {
        const val NEXT_SCREEN = "trainingPlanScreen"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingPlansBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.recyclerViewTrainingPlans
        val trainingPlanNamesString = dataBase.getColumn(PlansDataBaseHelper.TABLE_NAME, PlansDataBaseHelper.PLAN_NAME_COLUMN, PlansDataBaseHelper.PLAN_ID_COLUMN)
        trainingPlansNames = dataBase.convertList(trainingPlanNamesString){TrainingPlan(it)}
        if(!dataBase.isTableNotEmpty())
        {
            trainingPlansNames.add((TrainingPlan("You don't have any training plans yet.")))
        }
        trainingPlansRecyclerViewAdapter = TrainingPlansRecyclerViewAdapter(trainingPlansNames)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = trainingPlansRecyclerViewAdapter

        binding.buttonCreateTrainingPlan.setOnClickListener()
        {
            showEditTextDialog()
        }

        trainingPlansRecyclerViewAdapter.setOnClickListener(object :
            TrainingPlansRecyclerViewAdapter.OnClickListener {
            override fun onClick(position: Int, model: TrainingPlan) {
                val explicitIntent = Intent(applicationContext, TrainingPlanActivity::class.java)
                explicitIntent.putExtra(NEXT_SCREEN, model.name)
                startActivity(explicitIntent)
            }
        })
    }

    @SuppressLint("InflateParams")
    private fun showEditTextDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogLayout = layoutInflater.inflate(R.layout.enter_name_edit_text, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editTextName)
        editText.hint = "Plan name"

        with(builder) {
            setTitle("Enter training plan name")
                setPositiveButton("Add") { _, _ ->
                    try {
                        if (editText.text.isBlank()) {
                            throw ValidationException("Training plan name cannot be empty")
                        }
                        for (item in trainingPlansNames)
                        {
                            if(editText.text.toString() == item.toString())
                            {
                                throw ValidationException("There is already a plan with this name")
                            }
                        }
                        dataBase.addPLan(editText.text.toString())
                        trainingPlansNames.add(TrainingPlan(editText.text.toString()))
                        trainingPlansRecyclerViewAdapter.notifyItemInserted(trainingPlansRecyclerViewAdapter.itemCount)
                    }catch (exception: ValidationException)
                    {
                        Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
                    }
                }
            setNegativeButton("Cancel") { _, _ -> }
            setView(dialogLayout)
            show()
        }
    }

}