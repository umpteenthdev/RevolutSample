package com.umpteenthdev.revolutsample.core.usecases

import com.umpteenthdev.revolutsample.core.entity.RevException

interface ExceptionMapper {
    fun mapException(t: Throwable): RevException
}
