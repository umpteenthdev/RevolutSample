package com.umpteenthdev.revolutsample.core.outer.android

class RegexInputFilter(private val pattern: Regex) : SuggestedInputFilter() {
    override fun accept(suggestedResult: CharSequence): Boolean = pattern.matches(suggestedResult)
}
