package ir.mehdisekoba.cuteotp.internal.semantics

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import ir.mehdisekoba.cuteotp.OtpState

/**
 * Applies accessibility semantics to the OTP input component.
 *
 * This modifier sets the content description based on the current state and value,
 * ensuring that screen readers provide accurate feedback to the user.
 *
 * @param value The current string value entered in the OTP fields.
 * @param otpLength The total length of the expected OTP code.
 * @param state The current [OtpState] (Default, Error, or Success).
 * @param enabled Whether the OTP input is currently interactive.
 */
internal fun Modifier.otpSemantics(
    value: String,
    otpLength: Int,
    state: OtpState,
    enabled: Boolean
): Modifier = semantics {
    contentDescription = when (state) {
        OtpState.Default ->
            "OTP code, $value of $otpLength digits entered"

        OtpState.Error ->
            "Invalid OTP code, $value of $otpLength digits entered"

        OtpState.Success ->
            "OTP verification successful"
    }

    if (!enabled) {
        disabled()
    }
}
