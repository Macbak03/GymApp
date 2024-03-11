package com.example.gymapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.example.gymapp.R
import com.example.gymapp.layout.DynamicSizeSpinner
import com.example.gymapp.model.trainingPlans.TrainingPlan

class SpinnerArrayAdapter(context: Context, @LayoutRes layout: Int, private val entries: MutableList<TrainingPlan>) : ArrayAdapter<TrainingPlan>(context, layout, entries) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val selectedItemPosition = when (parent) {
            is AdapterView<*> -> parent.selectedItemPosition
            is DynamicSizeSpinner -> parent.selectedItemPosition
            else -> position
        }
        return makeLayout(selectedItemPosition, convertView, parent, R.layout.spinner_header)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return makeLayout(position, convertView, parent, R.layout.spinner_item)
    }

    private fun makeLayout(position: Int, convertView: View?, parent: ViewGroup, layout: Int): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(layout, parent, false)
        if (position != -1) {
            (view as? TextView)?.text = entries[position].toString()
        }
        return view
    }
}