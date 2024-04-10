package com.example.gymapp.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.compose.ui.unit.dp
import com.example.gymapp.R
import com.example.gymapp.databinding.FragmentChartsBinding
import com.example.gymapp.persistence.WorkoutHistoryDatabaseHelper
import com.example.gymapp.persistence.WorkoutSeriesDataBaseHelper
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.axis.horizontal.createHorizontalAxis
import com.patrykandpatrick.vico.core.axis.vertical.createVerticalAxis
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.context.DrawContext
import com.patrykandpatrick.vico.core.formatter.ValueFormatter
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.ExtraStore
import com.patrykandpatrick.vico.core.model.lineSeries
import com.patrykandpatrick.vico.views.chart.CartesianChartView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ChartsFragment : Fragment() {

    private var _binding: FragmentChartsBinding? = null
    private val binding get() = _binding!!

    private lateinit var historyDataBase: WorkoutHistoryDatabaseHelper

    private lateinit var lineChartLoad: CartesianChartView
    private lateinit var lineChartReps: CartesianChartView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartsBinding.inflate(layoutInflater, container, false)

        lineChartLoad = binding.chartViewLoad
        lineChartReps = binding.chartViewReps

        historyDataBase = WorkoutHistoryDatabaseHelper(requireContext(), null)

        val items = historyDataBase.getExerciseNames()

        var selectedItem: String

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)

        binding.exerciseSelect.apply {
            setAdapter(adapter)

            threshold = 1

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().isEmpty()) {
                        postDelayed({
                            if (s.toString()
                                    .isEmpty()
                            ) {
                                showDropDown()
                            }
                        }, 100)
                    }
                }

            })

            setOnTouchListener { _, _ ->
                performClick()
                showDropDown()
                false
            }

            setOnItemClickListener { parent, _, position, _ ->
                selectedItem = parent.adapter.getItem(position).toString()
                setChart(selectedItem)
            }
        }



        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun setChart(selectedExercise: String) {
        if (selectedExercise.isNotBlank()) {
            val cartesianChartModelProducerLoad = CartesianChartModelProducer.build()
            lineChartLoad.modelProducer = cartesianChartModelProducerLoad


            val dates = historyDataBase.getExercisesToChart(selectedExercise).second
            val exerciseIds = historyDataBase.getExercisesToChart(selectedExercise).first

            val loadValues = ArrayList<Float>()
            val repsValues = ArrayList<Float>()

            val workoutSeriesDatabase = WorkoutSeriesDataBaseHelper(requireContext(), null)

            for (exerciseId in exerciseIds) {
                val load = workoutSeriesDatabase.getChartData(exerciseId).second
                val reps = workoutSeriesDatabase.getChartData(exerciseId).first

                loadValues.add(load)
                repsValues.add(reps)
            }

            val dateTimeFormatter = DateTimeFormatter.ofPattern("d MMM")
            val sortedDates = dates.map{ LocalDate.parse(it) }.sorted()
            val labels = sortedDates.map { it.format(dateTimeFormatter) }

            val dateToXValue = sortedDates.withIndex().associate { it.value to it.index.toFloat() }
            val dataLoad = sortedDates.mapIndexed{index, localDate -> dateToXValue[localDate] to loadValues[index] }.toMap()
            val dataReps = sortedDates.mapIndexed{index, localDate -> dateToXValue[localDate] to repsValues[index] }.toMap()



            val bottomAxisValueFormatter =
                AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _, _ ->
                    labels.getOrElse(x.toInt()) { " " }
                }
           with(binding.chartViewLoad.chart?.bottomAxis as HorizontalAxis){
               valueFormatter = bottomAxisValueFormatter
           }
            with(binding.chartViewReps.chart?.bottomAxis as HorizontalAxis){
                valueFormatter = bottomAxisValueFormatter
            }

            cartesianChartModelProducerLoad.tryRunTransaction {
                lineSeries {
                    series(dataLoad.values)
                }
            }


            val cartesianChartModelProducerReps = CartesianChartModelProducer.build()
            lineChartReps.modelProducer = cartesianChartModelProducerReps


            cartesianChartModelProducerReps.tryRunTransaction {
                lineSeries {
                    series(dataReps.values)
                }
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
