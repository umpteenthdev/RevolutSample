package com.umpteenthdev.revolutsample.core.outer.android

import android.text.InputFilter
import android.text.Spanned

abstract class SuggestedInputFilter : InputFilter {

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        val suggestedResult = dest.replaceRange(dstart, dend, source)
        val cancelResult = dest.slice(dstart until dend)
        return if (accept(suggestedResult)) null else cancelResult
    }

    abstract fun accept(suggestedResult: CharSequence): Boolean
}
