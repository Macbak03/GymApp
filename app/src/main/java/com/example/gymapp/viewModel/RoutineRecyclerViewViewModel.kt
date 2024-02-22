package com.example.gymapp.viewModel

import androidx.lifecycle.ViewModel

class RoutineRecyclerViewViewModel : ViewModel() {
    private val editTextContents = mutableMapOf<Long, String>()

    fun saveEditTextContent(id: Long, content: String) {
        editTextContents[id] = content
    }

    fun getEditTextContent(id: Long): String {
        return editTextContents[id] ?: ""
    }

    fun clearEditTextContent(id: Long){
        editTextContents.remove(id)
    }

    fun hasId(id: Long): Boolean {
        return editTextContents.containsKey(id)
    }

    fun isViewModelEmpty(): Boolean{
        return editTextContents.isEmpty()
    }
}