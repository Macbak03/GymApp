package com.pl.Maciejbak.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pl.Maciejbak.R
import com.pl.Maciejbak.adapter.TrainingPlanRecyclerViewAdapter
import com.pl.Maciejbak.databinding.ActivityTrainingPlanBinding
import com.pl.Maciejbak.fragment.TrainingPlansFragment
import com.pl.Maciejbak.model.trainingPlans.TrainingPlanElement
import com.pl.Maciejbak.persistence.PlansDataBaseHelper
import com.pl.Maciejbak.persistence.RoutinesDataBaseHelper
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.io.InputStream

class TrainingPlanActivity : BaseActivity() {
    private lateinit var binding: ActivityTrainingPlanBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var trainingPlanRecyclerViewAdapter: TrainingPlanRecyclerViewAdapter
    private lateinit var loadCsvButton: Button // Deklaracja przycisku

    private var planName: String? = null
    private var routines: MutableList<TrainingPlanElement> = ArrayList()
    private val routinesDataBase = RoutinesDataBaseHelper(this, null)
    private val plansDataBase = PlansDataBaseHelper(this, null)
    private val defaultElement = "Create Routine"

    private val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.absoluteAdapterPosition
            if (direction == ItemTouchHelper.RIGHT) {
                showDeleteDialog(position)
            }
        }


        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            RecyclerViewSwipeDecorator.Builder(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
                .addBackgroundColor(
                    ContextCompat.getColor(
                        this@TrainingPlanActivity,
                        R.color.red
                    )
                )
                .addActionIcon(R.drawable.baseline_delete_24)
                .setActionIconTint(0xFFFFFFFF.toInt())
                .addCornerRadius(TypedValue.COMPLEX_UNIT_DIP, 3)
                .addPadding(TypedValue.COMPLEX_UNIT_DIP, 6f, 14f, 6f)
                .addSwipeRightLabel("Delete")
                .setSwipeRightLabelColor(0xFFFFFFFF.toInt())
                .setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    companion object {
        const val PLAN_NAME = "com.pl.Maciejbak.planname"
        const val ROUTINE_NAME = "com.pl.Maciejbak.routinename"
    }

    private val startCreateRoutineActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                routines.clear()
                setRecyclerViewContent()
            }
        }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    loadExercisesFromCSV(uri) // Ładowanie ćwiczeń z wybranego pliku
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        loadTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(TrainingPlansFragment.NEXT_SCREEN)) {
            binding.textViewTrainingPlanName.text =
                intent.getStringExtra(TrainingPlansFragment.NEXT_SCREEN)
        }
        planName = binding.textViewTrainingPlanName.text.toString()

        recyclerView = binding.RecyclerViewTrainingPlan
        trainingPlanRecyclerViewAdapter = TrainingPlanRecyclerViewAdapter(routines)

        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = trainingPlanRecyclerViewAdapter

        setRecyclerViewContent()

        val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.add_button_animation)

        binding.buttonAddRoutine.setOnClickListener {
            it.startAnimation(scaleAnimation)
            val explicitIntent = Intent(applicationContext, CreateRoutineActivity::class.java)
            explicitIntent.putExtra(PLAN_NAME, planName)
            startCreateRoutineActivityForResult.launch(explicitIntent)
        }

        trainingPlanRecyclerViewAdapter.setOnClickListener(object :
            TrainingPlanRecyclerViewAdapter.OnClickListener {
            override fun onClick(position: Int, model: TrainingPlanElement) {
                val explicitIntent = Intent(applicationContext, CreateRoutineActivity::class.java)
                if (routines[0].routineName == defaultElement) {
                    explicitIntent.putExtra(PLAN_NAME, planName)
                    startCreateRoutineActivityForResult.launch(explicitIntent)
                } else {
                    explicitIntent.putExtra(ROUTINE_NAME, model.routineName)
                    explicitIntent.putExtra(PLAN_NAME, planName)
                    startCreateRoutineActivityForResult.launch(explicitIntent)
                }
            }
        })
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // Inicjalizacja przycisku do ładowania CSV
        loadCsvButton = findViewById(R.id.loadCsvButton)

        // Ustawienie nasłuchiwacza na kliknięcie przycisku
        loadCsvButton.setOnClickListener {
            openFileChooser() // Uruchomienie wyboru pliku
        }

        binding.goBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setRecyclerViewContent() {
        val planId = plansDataBase.getPlanId(planName)
        if (planId != null) {
            if (!routinesDataBase.isPlanNotEmpty(planId.toString())) {
                routines.add(TrainingPlanElement(defaultElement))
            }
            val routinesInPlan = routinesDataBase.getRoutinesInPlan(planId)
            for (routine in routinesInPlan) {
                routines.add(routine)
            }
            trainingPlanRecyclerViewAdapter.notifyDataSetChanged()
        }
    }

    private fun showDeleteDialog(position: Int) {
        val builder = this.let { AlertDialog.Builder(it, R.style.YourAlertDialogTheme) }
        val dialogLayout = layoutInflater.inflate(R.layout.text_view_dialog_layout, null)
        val textView = dialogLayout.findViewById<TextView>(R.id.textViewDialog)
        val message = "${routines[position].routineName}?"
        textView.text = message
        with(builder) {
            this.setTitle("Are you sure you want to delete")
            this.setPositiveButton("Yes") { _, _ ->
                val planId = plansDataBase.getPlanId(planName)
                if (planId != null) {
                    trainingPlanRecyclerViewAdapter.deleteSingleRoutine(position)
                    routinesDataBase.deleteRoutines(planId, trainingPlanRecyclerViewAdapter.getDeletedRoutines())
                }
            }
            this.setNegativeButton("Cancel") { _, _ ->
                trainingPlanRecyclerViewAdapter.notifyItemChanged(position)
            }
            this.setView(dialogLayout)
            this.show()
        }
    }

    // Funkcja do otwierania okna wyboru pliku
    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*" // Akceptuj wszystkie typy plików
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("text/csv", "text/comma-separated-values", "application/csv")) // Obsługuj różne rozszerzenia CSV
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        filePickerLauncher.launch(intent) // Uruchomienie selektora plików
    }


    private fun loadExercisesFromCSV(inputStream: InputStream): List<TrainingPlanElement> {
        val exercises = mutableListOf<TrainingPlanElement>()
        inputStream.bufferedReader().use { reader ->
            val headerLine = reader.readLine() // Przeczytaj pierwszy wiersz
            val routineNames = headerLine.split(",") // Rozdziel nazwy rutyn

            // Dodaj wszystkie niepuste nazwy rutyn jako TrainingPlanElement
            for (routineName in routineNames) {
                val trimmedName = routineName.trim() // Usuń białe znaki
                if (trimmedName.isNotEmpty()) { // Sprawdź, czy nazwa nie jest pusta
                    exercises.add(TrainingPlanElement(trimmedName)) // Dodaj do listy
                }
            }

            // Przeczytaj pozostałe wiersze, jeśli są potrzebne
            reader.forEachLine { line ->
                // Możesz dodać logikę do przetwarzania pozostałych wierszy, jeśli to konieczne
            }
        }
        return exercises // Zwróć załadowane ćwiczenia
    }



    // Zaktualizowana funkcja do ładowania ćwiczeń z CSV
    private fun loadExercisesFromCSV(uri: Uri) {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val exercises = loadExercisesFromCSV(inputStream) // Ładowanie ćwiczeń z pliku
            Toast.makeText(this, "Loaded ${exercises.size} exercises", Toast.LENGTH_SHORT).show()
            routines.addAll(exercises) // Dodaj nowe ćwiczenia do listy
            trainingPlanRecyclerViewAdapter.notifyDataSetChanged() // Powiadom adapter o zmianach
        } ?: run {
            Toast.makeText(this, "Failed to open file", Toast.LENGTH_SHORT).show()
        }
    }
}
