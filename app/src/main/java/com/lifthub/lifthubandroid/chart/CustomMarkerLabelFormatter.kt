package com.lifthub.lifthubandroid.chart

import com.lifthub.lifthubandroid.model.routine.WeightUnit
import com.patrykandpatrick.vico.core.chart.values.ChartValues
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerLabelFormatter

class CustomMarkerLabelFormatter(
    private val reps: List<Float>,
    private val weight: List<Float>,
    private val weightUnit: WeightUnit
) : MarkerLabelFormatter {
    override fun getLabel(
        markedEntries: List<Marker.EntryModel>,
        chartValues: ChartValues
    ): CharSequence {
        val labelBuilder = StringBuilder()
        for (entry in markedEntries) {
            val index = entry.index
            val repsValue = if (reps[index] % 1 == 0f) {
                String.format("%.0f", reps[index])
            } else {
                String.format("%.2f", reps[index])
            }

            val weightValue = if (weight[index] % 1 == 0f) {
                String.format("%.0f", weight[index])
            } else {
                String.format("%.2f", weight[index])
            }
            if (reps[index] <= 1f) {
                labelBuilder.append("$weightValue ").append(weightUnit.toString()).append("\n")
                    .append("$repsValue ").append("rep").append("\n")

            } else {
                labelBuilder.append("$weightValue ").append(weightUnit.toString()).append("\n")
                    .append("$repsValue ").append("reps").append("\n")

            }
        }
        return labelBuilder.toString()
    }


}