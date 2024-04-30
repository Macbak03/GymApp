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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.preference.PreferenceManager
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
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.lineSeries
import com.example.gymapp.chart.rememberMarker
import com.example.gymapp.model.routine.WeightUnit
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.scroll.Scroll
import com.patrykandpatrick.vico.views.chart.CartesianChartView
import com.patrykandpatrick.vico.views.scroll.ScrollHandler
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ChartsFragment : Fragment() {

    private lateinit var historyDataBase: WorkoutHistoryDatabaseHelper
    private lateinit var workoutSeriesDataBase: WorkoutSeriesDataBaseHelper

    private lateinit var lineChartLoad: CartesianChartView
    private lateinit var customMarker: MarkerComponent

    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var items: List<String>
    private var defaultWeightUnit = WeightUnit.kg

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_charts, container, false).apply {
            findViewById<ComposeView>(R.id.composeView).setContent {
                customMarker = rememberMarker()
            }
            lineChartLoad = findViewById(R.id.chartViewLoad)


            historyDataBase = WorkoutHistoryDatabaseHelper(requireContext(), null)
            workoutSeriesDataBase = WorkoutSeriesDataBaseHelper(requireContext(), null)

            items = historyDataBase.getExerciseNames()

            var selectedItem: String

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)

            autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.exerciseSelect).apply {
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

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

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
        val selectedExercise = autoCompleteTextView.text.toString()
        super.onResume()
        if(items.contains(selectedExercise))
        {
            setChart(selectedExercise)
        }else{
            lineChartLoad.visibility = View.GONE
        }
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun setChart(selectedExercise: String) {
        loadUnitSettings()
        lineChartLoad.visibility = View.VISIBLE
        if (selectedExercise.isNotBlank()) {
            val cartesianChartModelProducerLoad = CartesianChartModelProducer.build()
            lineChartLoad.modelProducer = cartesianChartModelProducerLoad

            val dates = historyDataBase.getExercisesToChart(selectedExercise).second
            val exerciseIds = historyDataBase.getExercisesToChart(selectedExercise).first

            val loadValues = ArrayList<Float>()
            val repsValues = ArrayList<Float>()


            for (exerciseId in exerciseIds) {
                val load = adaptLoadToUnit(exerciseId)
                val reps = workoutSeriesDataBase.getChartData(exerciseId).first

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

            val minLoadValue = getMinValue(loadValues)
            val maxLoadValue = getMaxValue(loadValues, 10)

            val bottomAxisValueFormatter =
                AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _, _ ->
                    labels.getOrElse(x.toInt()) { " " }
                }

            val loadAxisValueOverrider = AxisValueOverrider.fixed(
                minY = minLoadValue.toFloat(),
                maxY = maxLoadValue.toFloat()
            )

            val markerFormatter = CustomMarkerLabelFormatter(repsValues)
            customMarker.labelFormatter = markerFormatter

            val scrollHandler = ScrollHandler(initialScroll = Scroll.Absolute.End)
            lineChartLoad.scrollHandler = scrollHandler

            with(lineChartLoad) {
                (chart?.layers?.get(0) as LineCartesianLayer?)?.axisValueOverrider =
                    loadAxisValueOverrider
                (chart?.bottomAxis as HorizontalAxis?)?.valueFormatter = bottomAxisValueFormatter
                (chart?.startAxis as VerticalAxis?)?.titleComponent = TextComponent.build {
                    color = Color.White.toArgb()
                    textSizeSp = 14f
                }
                (chart?.startAxis as VerticalAxis?)?.title = defaultWeightUnit.toString()
                marker = customMarker
            }

            cartesianChartModelProducerLoad.tryRunTransaction {
                lineSeries {
                    series(dataLoad.values)
                }
            }

        }

    }

    private fun adaptLoadToUnit(exerciseId: Int): Float{
        val multiplier = 2.205f
        var load = workoutSeriesDataBase.getChartData(exerciseId).second
        val unit = historyDataBase.getLoadUnit(exerciseId)
        if (defaultWeightUnit.toString() == "kg" && unit == "lbs"){
            load /= multiplier
        }else if(defaultWeightUnit.toString() == "lbs" && unit == "kg"){
            load *= multiplier
        }
        return load
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

    private fun loadUnitSettings(){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        when(sharedPreferences.getString("unit", ""))
        {
            "kg" -> defaultWeightUnit = WeightUnit.kg
            "lbs" -> defaultWeightUnit = WeightUnit.lbs
        }

    }

}
