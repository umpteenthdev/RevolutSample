package com.umpteenthdev.revolutsample.core.entity

import android.os.Build
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.umpteenthdev.revolutsample.core.BuildConfig
import java.io.PrintWriter
import java.io.StringWriter

private const val TAG_PREFIX = "#RS"
private const val MAX_TAG_LENGTH = 23
private const val MAX_MESSAGE_LENGTH = 4000

/**
 * Logs [message] to Crashlytics and prints it to LogCat with [Log.INFO] priority
 */
fun Any.log(message: String, tag: String? = null) {
    logInternal(
        priority = Log.INFO,
        message = message,
        tag = tag,
        logToCrashlytics = true,
        caller = this
    )
}

/**
 * Sends [throwable] to Crashlytics as non-critical exception,
 * then logs stacktrace to Crashlytics and prints it to LogCat with [Log.ASSERT] priority
 */
fun Any.sendNonFatalException(throwable: Throwable) {
    Crashlytics.logException(throwable)
    logInternal(
        priority = Log.ASSERT,
        throwable = throwable,
        logToCrashlytics = true,
        caller = this
    )
}

/**
 * Logs stacktrace to Crashlytics and prints it to LogCat with [Log.ERROR] priority
 */
fun Any.log(throwable: Throwable) {
    logInternal(
        priority = Log.WARN,
        throwable = throwable,
        logToCrashlytics = true,
        caller = this
    )
}

/**
 * Prints [message] to LogCat with [Log.DEBUG] priority
 */
fun Any.logd(message: String, tag: String? = null) {
    logInternal(
        priority = Log.DEBUG,
        message = message,
        tag = tag,
        logToCrashlytics = false,
        caller = this
    )
}

/**
 * Prints stacktrace to LogCat with [Log.WARN] priority
 */
fun Any.logd(throwable: Throwable) {
    logInternal(
        priority = Log.WARN,
        throwable = throwable,
        logToCrashlytics = false,
        caller = this
    )
}

private fun logInternal(priority: Int, message: String, tag: String?, logToCrashlytics: Boolean, caller: Any) {
    if ((!BuildConfig.DEBUG && !logToCrashlytics) || message.isBlank()) {
        return
    }

    val preparedTag = prepareTag(caller, tag)

    if (logToCrashlytics) {
        Crashlytics.log("$preparedTag: $message")
    }

    if (BuildConfig.DEBUG) {
        logToLogcat(
            priority = priority,
            tag = preparedTag,
            message = message
        )
    }
}

private fun logInternal(priority: Int, throwable: Throwable, logToCrashlytics: Boolean, caller: Any) {
    if (!BuildConfig.DEBUG && !logToCrashlytics) {
        return
    }

    val preparedTag = prepareTag(caller, null)
    val message = getStackTraceString(throwable)

    if (logToCrashlytics) {
        Crashlytics.log("$preparedTag: $message")
    }

    if (BuildConfig.DEBUG) {
        logToLogcat(
            priority = priority,
            tag = preparedTag,
            message = message
        )
    }
}

private fun prepareTag(caller: Any, tag: String?): String {
    val preparedTag = if (tag != null) "${TAG_PREFIX}_$tag" else "${TAG_PREFIX}_${getClassName(caller)}"

    return if (preparedTag.length <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        preparedTag
    } else {
        preparedTag.substring(0, MAX_TAG_LENGTH)
    }
}

private fun getClassName(instance: Any): String = instance::class.java.name
    .substringAfterLast('.')
    .replace("""(\$\d+)+$""".toRegex(), "") // Remove digits from anonymous classes names

private fun getStackTraceString(t: Throwable): String {
    val sw = StringWriter(256)
    val pw = PrintWriter(sw, false)
    t.printStackTrace(pw)
    pw.flush()
    return sw.toString()
}

private fun logToLogcat(priority: Int, tag: String, message: String) {
    if (message.length < MAX_MESSAGE_LENGTH) {
        Log.println(priority, tag, message)
    } else {
        message.chunked(MAX_MESSAGE_LENGTH).forEach { shortMessage ->
            Log.println(priority, tag, shortMessage)
        }
    }
}
