package com.umpteenthdev.revolutsample.core.entity

/**
 * Use for `when` blocks with sealed class to get compilation error on new subclass adding
 */
fun <T> T?.requireAllBranches(): T? = this
