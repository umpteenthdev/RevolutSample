package com.umpteenthdev.revolutsample.core.entity

sealed class RevException {
    object ConnectionError : RevException()
    class UnknownException(val cause: Throwable) : RevException()
}
