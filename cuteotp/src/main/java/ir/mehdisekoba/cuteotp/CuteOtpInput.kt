package ir.mehdisekoba.cuteotp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.mehdisekoba.cuteotp.internal.animation.OtpSuccessAnimation
import ir.mehdisekoba.cuteotp.internal.boxes.OtpBoxes
import ir.mehdisekoba.cuteotp.internal.controller.rememberOtpController
import ir.mehdisekoba.cuteotp.internal.input.OtpTextField
import ir.mehdisekoba.cuteotp.internal.semantics.otpSemantics

/**
 * A highly customizable and animated One-Time Password (OTP) input component for Jetpack Compose.
 *
 * @param otpLength The number of OTP digits, must be between 4 and 8.
 * @param value The current string value of the input, or null for an empty initial state.
 * @param onOtpComplete Callback invoked when the user has entered all digits.
 * @param onOtpChange Callback invoked whenever the input text changes.
 * @param modifier The [Modifier] to be applied to the layout.
 * @param otpState The current operational state (e.g., Default, Error, Success) determining the visual appearance.
 * @param colors The color configuration for different states of the OTP boxes.
 * @param textStyle The [TextStyle] applied to the entered digits.
 * @param boxSize The size (width and height) of each individual digit box.
 * @param boxSpacing The spacing between consecutive digit boxes.
 * @param shape The [Shape] of each individual digit box.
 * @param borderWidth The border thickness of each digit box.
 * @param enabled Controls the enabled state of the input. When false, user interaction is disabled.
 * @param autoFocus If true, the input will automatically request focus and open the keyboard when displayed.
 * @param hideKeyboardOnComplete If true, the software keyboard will automatically hide once all digits are entered.
 * @param enableSuccessAnimation If true, displays a success animation when [otpState] is set to [OtpState.Success].
 * @param successAnimationDuration The duration of the success animation in milliseconds.
 * @param readOnly Controls the read-only state. When true, the input cannot be modified but remains focusable.
 * @param contentDescription Optional custom accessibility description for screen readers.
 * @throws IllegalArgumentException if [otpLength] is not between 4 and 8.
 */
@Composable
fun CuteOtpInput(
    otpLength: Int = 4,
    value: String? = null,
    onOtpComplete: (String) -> Unit = {},
    onOtpChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    otpState: OtpState = OtpState.Default,
    colors: OtpColors = OtpColors.OtpStyle,
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        lineHeight = 22.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),
    boxSize: Dp = 56.dp,
    boxSpacing: Dp = 12.dp,
    shape: androidx.compose.ui.graphics.Shape = androidx.compose.foundation.shape.RoundedCornerShape(
        14.dp
    ),
    borderWidth: Dp = 1.25.dp,
    enabled: Boolean = true,
    autoFocus: Boolean = true,
    hideKeyboardOnComplete: Boolean = true,
    enableSuccessAnimation: Boolean = true,
    successAnimationDuration: Int = 3500,
    readOnly: Boolean = false,
    contentDescription: String? = null,
) {
    require(otpLength in 4..8) {
        "otpLength must be between 4 and 8"
    }

    val controller = rememberOtpController(
        value = value,
        otpLength = otpLength
    )
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current


    LaunchedEffect(controller.text) {
        onOtpChange(controller.text)

        if (controller.isComplete) {
            onOtpComplete(controller.text)

            if (hideKeyboardOnComplete) {
                keyboardController?.hide()
            }
        }
    }

    val description = contentDescription
        ?: "OTP input, $otpLength digits. Current value: ${
            controller.text.ifEmpty { "empty" }
        }"

    Box(
        modifier = modifier
            .heightIn(min = boxSize * 2.9f)
            .semantics(mergeDescendants = true) {
                this.contentDescription = description
                this.stateDescription = controller.text
            }
            .otpSemantics(
                value = controller.text,
                otpLength = otpLength,
                state = otpState,
                enabled = enabled
            ),
        contentAlignment = Alignment.Center
    ) {
        OtpTextField(
            controller = controller,
            otpLength = otpLength,
            enabled = enabled,
            readOnly = readOnly,
            otpState = otpState,
            autoFocus = autoFocus,
            focusRequester = focusRequester,
            keyboardController = keyboardController,
            modifier = Modifier
                .size(1.dp)
                .offset(x = (-3000).dp)
        )

        if (otpState == OtpState.Success && enableSuccessAnimation) {
            OtpSuccessAnimation(
                value = controller.text,
                otpLength = otpLength,
                colors = colors,
                textStyle = textStyle,
                boxSize = boxSize,
                boxSpacing = boxSpacing,
                shape = shape,
                borderWidth = borderWidth,
                durationMillis = successAnimationDuration,
                enabled = enabled
            )
        } else {
            OtpBoxes(
                value = controller.text,
                otpLength = otpLength,
                focusedIndex = controller.focusedIndex,
                otpState = otpState,
                colors = colors,
                textStyle = textStyle,
                boxSize = boxSize,
                boxSpacing = boxSpacing,
                shape = shape,
                borderWidth = borderWidth,
                enabled = enabled,
                onRequestFocus = {
                    focusRequester.requestFocus()
                    controller.moveCursorToEnd()
                }
            )
        }
    }
}
