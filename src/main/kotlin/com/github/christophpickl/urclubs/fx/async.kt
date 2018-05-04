package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.fx.AsyncResult.AsyncFailResult
import com.github.christophpickl.urclubs.fx.AsyncResult.AsyncSuccessResult
import javafx.scene.control.Alert.AlertType.ERROR
import javafx.scene.control.ButtonType
import tornadofx.*


sealed class AsyncResult<T> {
    class AsyncSuccessResult<T>(val data: T) : AsyncResult<T>()
    class AsyncFailResult<T>(val exception: Exception) : AsyncResult<T>()
}

private val log = LOG {}

private fun <T> defaultOnAny(@Suppress("UNUSED_PARAMETER") result: AsyncResult<T>) {
    // do nothing
}

data class FailDialogContent(
        val title: String,
        val header: String
)

fun <T> runAsyncSafely(
        onSuccess: (T) -> Unit,
        dialogContent: FailDialogContent,
        onAny: (AsyncResult<T>) -> Unit = ::defaultOnAny,
        asyncFunction: () -> T
) {
    runAsync {
        try {
            AsyncSuccessResult(asyncFunction())
        } catch (e: Exception) {
            AsyncFailResult<T>(e)
        }
    } ui { result: AsyncResult<T> ->
        onAny(result)
        when (result) {
            is AsyncSuccessResult<T> -> {
                onSuccess(result.data)
            }
            is AsyncFailResult<T> -> {
                log.error(result.exception) { "Failed to execute async task!" }
                alert(
                        type = ERROR,
                        title = dialogContent.title,
                        header = dialogContent.header,
                        content = result.exception.message,
                        buttons = *arrayOf(ButtonType.CLOSE)
                )
            }
        }

    }
}
