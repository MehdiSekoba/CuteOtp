package ir.mehdisekoba.cuteotp.internal.input

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import ir.mehdisekoba.cuteotp.OtpState
import ir.mehdisekoba.cuteotp.internal.controller.OtpController
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * Internal composable that wraps a [BasicTextField] to handle the hidden input logic for OTP.
 *
 * @param controller The [OtpController] managing the OTP state.
 * @param otpLength The number of characters expected in the OTP.
 * @param enabled Whether the text field is enabled.
 * @param readOnly Whether the text field is read-only.
 * @param otpState The current [OtpState] of the input (e.g., Success, Error).
 * @param autoFocus If true, the field will automatically request focus when composition starts.
 * @param focusRequester The [FocusRequester] used to manage focus for this field.
 * @param keyboardController The controller to manage the software keyboard.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
internal fun OtpTextField(
    controller: OtpController,
    otpLength: Int,
    enabled: Boolean,
    readOnly: Boolean,
    otpState: OtpState,
    autoFocus: Boolean,
    focusRequester: FocusRequester,
    keyboardController: SoftwareKeyboardController?,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(autoFocus, enabled, readOnly, otpState) {
        if (autoFocus && enabled && !readOnly && otpState != OtpState.Success) {
            delay(100.milliseconds)
            focusRequester.requestFocus()
        }
    }

    BasicTextField(
        value = controller.value,
        onValueChange = { newValue ->
            if (!enabled || readOnly || otpState == OtpState.Success) return@BasicTextField
            controller.updateFromTextField(newValue)
        },
        modifier = modifier
            .focusRequester(focusRequester)
            .semantics {
                contentDescription = "One time password"
            },
        enabled = enabled && !readOnly && otpState != OtpState.Success,
        readOnly = readOnly,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }
        ),
        singleLine = true,
        decorationBox = {}
    )
}
