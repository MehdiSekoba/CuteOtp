package ir.mehdisekoba.cuteotp.internal.controller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/**
 * Internal controller class that manages the state and logic for the OTP input.
 *
 * This class handles text sanitization, selection management, and synchronization
 * between external state and internal [TextFieldValue].
 *
 * @property initialValue The starting text for the OTP field.
 * @property otpLength The maximum allowed length of the OTP.
 */
internal class OtpController(
    initialValue: String,
    val otpLength: Int
) {
    /**
     * The current [TextFieldValue] containing the text and selection state. */
    var value by mutableStateOf(
        TextFieldValue(
            text = sanitize(initialValue),
            selection = TextRange(sanitize(initialValue).length)
        )
    )
        private set

    /**
     * Returns the current raw string text of the OTP.
     */
    val text: String
        get() = value.text

    /**
     * The index of the character currently being focused or edited.
     */
    val focusedIndex: Int
        get() = text.length.coerceAtMost(otpLength)

    /**
     * Returns true if the current text length matches the required [otpLength].
     */
    val isComplete: Boolean
        get() = text.length == otpLength

    /**
     * Updates the internal state from a new [TextFieldValue], applying sanitization.
     *
     * @param newValue The new value provided by the text input.
     */
    fun updateFromTextField(newValue: TextFieldValue) {
        val filtered = sanitize(newValue.text)
        value = newValue.copy(
            text = filtered,
            selection = TextRange(filtered.length)
        )
    }

    /**
     * Synchronizes the controller's state with an external string value.
     *
     * @param externalValue The new string to sync with.
     */
    fun syncExternalValue(externalValue: String) {
        val filtered = sanitize(externalValue)
        if (filtered != text) {
            value = TextFieldValue(
                text = filtered,
                selection = TextRange(filtered.length)
            )
        }
    }

    /**
     * Forces the text selection cursor to the end of the current text.
     */
    fun moveCursorToEnd() {
        value = value.copy(selection = TextRange(text.length))
    }

    /**
     * Sanitizes the input by filtering for digits only and truncating to [otpLength].
     *
     * @param input The raw input string.
     * @return A sanitized string containing only digits up to [otpLength].
     */
    private fun sanitize(input: String): String =
        input.filter(Char::isDigit).take(otpLength)
}

/**
 * Creates and remembers an [OtpController] instance across recompositions.
 *
 * @param value The external state value to initialize or sync with.
 * @param otpLength The fixed length of the OTP.
 * @return A remembered [OtpController] instance.
 */
@Composable
internal fun rememberOtpController(
    value: String?,
    otpLength: Int
): OtpController {
    val controller = remember(otpLength) {
        OtpController(
            initialValue = value.orEmpty(),
            otpLength = otpLength
        )
    }

    androidx.compose.runtime.LaunchedEffect(value, otpLength) {
        if (value != null) {
            controller.syncExternalValue(value)
        }
    }

    return controller
}
