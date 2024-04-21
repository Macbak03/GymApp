package com.example.gymapp.chart

import com.patrykandpatrick.vico.core.chart.values.ChartValues
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerLabelFormatter

class CustomMarkerLabelFormatter(private val data: List<Float>): MarkerLabelFormatter{
    override fun getLabel(
        markedEntries: List<Marker.EntryModel>,
        chartValues: ChartValues
    ): CharSequence {
        val labelBuilder = StringBuilder()
        for (entry in markedEntries) {
            val index = entry.index
            val displayValue = if (data[index] % 1 == 0f){
                String.format("%.0f", data[index])
            } else{
                String.format("%.2f", data[index])
            }
            if(data[index] <= 1f){
                labelBuilder.append("$displayValue ").append("rep").append("\n")
            }else{
                labelBuilder.append("$displayValue ").append("reps").append("\n")
            }
        }
        return labelBuilder.toString()
    }


}