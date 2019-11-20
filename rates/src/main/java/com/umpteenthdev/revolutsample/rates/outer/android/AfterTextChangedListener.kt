package com.umpteenthdev.revolutsample.rates.outer.android

import android.text.TextWatcher

abstract class AfterTextChangedListener : TextWatcher {

    final override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // do nothing
    }

    final override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // do nothing
    }
}
