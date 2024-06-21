package com.lifthub.lifthubandroid.exception

import android.widget.EditText

class ValidationException : RuntimeException {
    var viewId: Int = 0

    constructor(message: String) : super(message)
    constructor(message: String, viewId: Int) : super(message) {
        this.viewId = viewId
    }

    fun highlightError(editText: EditText?) {
        editText?.let {
            /* val expandableListView = it.parent as? ExpandableListView
             expandableListView?.scrollTo(0, it.top)*/
            it.requestFocus()
        }
    }
}
