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
import android.widget.AutoCompleteTextView
import androidx.compose.ui.platform.ComposeView
import com.example.gymapp.R
import com.example.gymapp.chart.CustomMarkerLabelFormatter
import com.example.gymapp.persistence.WorkoutHistoryDatabaseHelper
import com.example.gymapp.persistence.WorkoutSeriesDataBaseHelper
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.chart.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.chart.values.AxisValueOverrider
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.model.CartesianChartModel
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.LineCartesianLayerModel
import com.patrykandpatrick.vico.core.model.lineSeries
import com.example.gymapp.chart.rememberMarker
import com.patrykandpatrick.vico.views.chart.CartesianChartView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ChartsFragment : Fragment() {

    private lateinit var historyDataBase: WorkoutHistoryDatabaseHelper

    private lateinit var lineChartLoad: CartesianChartView
    private lateinit var lineChartReps: CartesianChartView
    private lateinit var customMarker: MarkerComponent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_charts, container, false).apply {
            findViewById<ComposeView>(R.id.composeView).setContent {
                customMarker = rememberMarker()
            }
            lineChartLoad = findViewById(R.id.chartViewLoad)
            lineChartReps = findViewById(R.id.chartViewReps)


            historyDataBase = WorkoutHistoryDatabaseHelper(requireContext(), null)

            val items = historyDataBase.getExerciseNames()

            var selectedItem: String

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)

            findViewById<AutoCompleteTextView>(R.id.exerciseSelect).apply {
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

        }
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
            val sortedDates = dates.map { LocalDate.parse(it) }.sorted()
            val labels = sortedDates.map { it.format(dateTimeFormatter) }

            val dateToXValue = sortedDates.withIndex().associate { it.value to it.index.toFloat() }
            val dataLoad =
                sortedDates.mapIndexed { index, localDate -> dateToXValue[localDate] to loadValues[index] }
                    .toMap()
            val dataReps =
                sortedDates.mapIndexed { index, localDate -> dateToXValue[localDate] to repsValues[index] }
                    .toMap()

            val minLoadValue = getMinValue(loadValues)
            val maxLoadValue = getMaxValue(loadValues, 10)

            val maxRepValue = getMaxValue(repsValues, 1)

            val bottomAxisValueFormatter =
                AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _, _ ->
                    labels.getOrElse(x.toInt()) { " " }
                }

            val loadAxisValueOverrider = AxisValueOverrider.fixed(
                minY = minLoadValue.toFloat(),
                maxY = maxLoadValue.toFloat()
            )
            val repsAxisValueOverrider = AxisValueOverrider.fixed(maxY = maxRepValue.toFloat())

            CartesianChartModel(
                LineCartesianLayerModel.build { series(dataLoad.values) },
                LineCartesianLayerModel.build { series(dataReps.values) })

            val markerFormatter = CustomMarkerLabelFormatter(repsValues)
            customMarker.labelFormatter = markerFormatter

            with(lineChartLoad) {
                (chart?.layers?.get(0) as LineCartesianLayer?)?.axisValueOverrider =
                    loadAxisValueOverrider
                (chart?.bottomAxis as HorizontalAxis?)?.valueFormatter = bottomAxisValueFormatter
                marker = customMarker
            }

            /*           with(lineChartReps){
                           (chart?.layers?.get(0) as LineCartesianLayer?)?.axisValueOverrider = repsAxisValueOverrider
                           (chart?.bottomAxis as HorizontalAxis?)?.valueFormatter = bottomAxisValueFormatter
                       }*/

            cartesianChartModelProducerLoad.tryRunTransaction {
                lineSeries {
                    series(dataLoad.values)
                }
            }


            /* val cartesianChartModelProducerReps = CartesianChartModelProducer.build()
             lineChartReps.modelProducer = cartesianChartModelProducerReps


             cartesianChartModelProducerReps.tryRunTransaction {
                 lineSeries {
                     series(dataReps.values)
                 }
             }*/
        }

    }

    private fun getMinValue(data: List<Float>): Int {
        val min = data.min()
        val offset = min * 0.1
        return (min - offset).toInt().roundToClosest(10)
    }

    private fun getMaxValue(data: List<Float>, roundStep: Int): Int {
        val max = data.max()
        val offset = max * 0.1
        return if (max > roundStep + roundStep / 2) {
            (max + offset).toInt().roundToClosest(roundStep)
        } else {
            (max + offset).toInt().roundToClosest(1)
        }
    }

    private fun Int.roundToClosest(step: Int): Int {
        require(step > 0)
        val lower = this - (this % step)
        val upper = lower + if (this >= 0) step else -step
        return if (this - lower < upper - this) lower else upper
    }

}
