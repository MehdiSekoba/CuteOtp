package ir.mehdisekoba.cuteotp.internal.boxes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import ir.mehdisekoba.cuteotp.OtpColors
import ir.mehdisekoba.cuteotp.OtpState

/**
 * A container that renders a row of individual OTP input boxes.
 *
 * @param value The current text value of the entire OTP input.
 * @param otpLength The total number of OTP digits/boxes to display.
 * @param focusedIndex The index of the currently focused box.
 * @param otpState The current state of the OTP (Normal, Error, or Success).
 * @param colors The color configuration for different states of the OTP boxes.
 * @param textStyle The text style to be applied to the digits inside the boxes.
 * @param boxSize The size (width and height) of each individual box.
 * @param boxSpacing The space between each box.
 * @param shape The shape of the boxes (e.g., rounded corners).
 * @param borderWidth The thickness of the box border.
 * @param enabled Whether the OTP input is interactive.
 * @param onRequestFocus Callback triggered when a box is clicked to request focus on the hidden text field.
 * @param modifier The modifier to be applied to the Row container.
 */
@Composable
internal fun OtpBoxes(
    value: String,
    otpLength: Int,
    focusedIndex: Int,
    otpState: OtpState,
    colors: OtpColors,
    textStyle: TextStyle,
    boxSize: Dp,
    boxSpacing: Dp,
    shape: Shape,
    borderWidth: Dp,
    enabled: Boolean,
    onRequestFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(boxSpacing)
    ) {
        repeat(otpLength) { index ->
            val digit = value.getOrNull(index)?.toString().orEmpty()

            OtpBox(
                value = digit,
                isFocused = index == focusedIndex &&
                    enabled &&
                    otpState != OtpState.Success,
                isFilled = digit.isNotEmpty(),
                otpState = otpState,
                colors = colors,
                textStyle = textStyle,
                boxSize = boxSize,
                shape = shape,
                borderWidth = borderWidth,
                index = index,
                otpLength = otpLength,
                onClick = onRequestFocus
            )
        }
    }
}
