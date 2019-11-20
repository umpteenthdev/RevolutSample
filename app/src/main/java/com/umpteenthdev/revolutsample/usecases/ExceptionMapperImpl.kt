package com.umpteenthdev.revolutsample.usecases

import com.umpteenthdev.revolutsample.core.entity.RevException
import com.umpteenthdev.revolutsample.core.usecases.ExceptionMapper
import java.io.EOFException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.net.ssl.SSLException

class ExceptionMapperImpl @Inject constructor() : ExceptionMapper {

    override fun mapException(t: Throwable): RevException = when (t) {
        is SocketTimeoutException,
        is UnknownHostException,
        is ConnectException,
        is EOFException,
        is SSLException -> RevException.ConnectionError

        else -> RevException.UnknownException(t)
    }
}
