package ir.mehdisekoba.cuteotp.internal.boxes

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ir.mehdisekoba.cuteotp.OtpColors
import ir.mehdisekoba.cuteotp.OtpState

/**
 * A single digit container for the OTP input.
 * Handles its own animations for state changes (focus, error, success, filling)
 * and entry animations when a character is typed.
 *
 * @param value The character to display in this box.
 * @param isFocused Whether this box currently has input focus.
 * @param isFilled Whether this box contains a character.
 * @param otpState The current overall state of the OTP (Default, Error, Success).
 * @param colors The color configuration for various states.
 * @param textStyle The style applied to the character text.
 * @param boxSize The width and height of the box.
 * @param shape The shape of the box (corners, etc.).
 * @param borderWidth The thickness of the box border.
 * @param index The zero-based position of this box in the OTP sequence.
 * @param otpLength The total number of digits in the OTP.
 * @param onClick Callback triggered when the box is tapped.
 */
@Composable
internal fun OtpBox(
    value: String,
    isFocused: Boolean,
    isFilled: Boolean,
    otpState: OtpState,
    colors: OtpColors,
    textStyle: TextStyle,
    boxSize: Dp,
    shape: Shape,
    borderWidth: Dp,
    index: Int,
    otpLength: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = when {
            otpState == OtpState.Error -> colors.errorBorder
            otpState == OtpState.Success -> colors.successBorder
            isFocused -> colors.focusedBorder
            isFilled -> colors.filledBorder
            else -> colors.unfocusedBorder
        },
        animationSpec = tween(180),
        label = "otpBorder"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            otpState == OtpState.Error -> colors.errorBackground
            otpState == OtpState.Success -> colors.successBackground
            isFocused -> colors.focusedBackground
            isFilled -> colors.filledBackground
            else -> colors.unfocusedBackground
        },
        animationSpec = tween(180),
        label = "otpBackground"
    )

    val textColor by animateColorAsState(
        targetValue = when (otpState) {
            OtpState.Error -> colors.errorText
            OtpState.Success -> colors.successText
            else -> colors.text
        },
        animationSpec = tween(180),
        label = "otpText"
    )

    val activeBorderWidth =
        if (isFocused && otpState == OtpState.Default) {
            borderWidth + 0.5.dp
        } else {
            borderWidth
        }

    val scale = remember { Animatable(1f) }
    val entryStroke = remember { Animatable(0f) }

    LaunchedEffect(value) {
        if (value.isNotEmpty()) {
            entryStroke.snapTo(0f)

            scale.animateTo(
                targetValue = 1.07f,
                animationSpec = tween(55)
            )

            entryStroke.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 340,
                    easing = FastOutSlowInEasing
                )
            )

            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.52f,
                    stiffness = 450f
                )
            )

            entryStroke.animateTo(
                targetValue = 1.22f,
                animationSpec = tween(
                    durationMillis = 140,
                    easing = LinearEasing
                )
            )
        } else {
            entryStroke.snapTo(0f)
            scale.snapTo(1f)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(boxSize)
            .scale(scale.value)
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = activeBorderWidth,
                color = borderColor,
                shape = shape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = otpState != OtpState.Success
            ) {
                onClick()
            }
            .semantics {
                contentDescription =
                    "Digit ${index + 1} of $otpLength" +
                        if (value.isNotEmpty()) ": $value" else ": empty"
            }
    ) {
        if (value.isNotEmpty()) {
            Text(
                text = value,
                style = textStyle.copy(
                    color = textColor,
                    textAlign = TextAlign.Center,
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                textAlign = TextAlign.Center
            )
        } else if (isFocused) {
            val infinite = rememberInfiniteTransition(label = "cursor")

            val alpha by infinite.animateFloat(
                initialValue = 1f,
                targetValue = 0.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(450, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "cursorAlpha"
            )

            Box(
                Modifier
                    .width(1.8.dp)
                    .height(boxSize * 0.38f)
                    .background(colors.cursorColor.copy(alpha = alpha))
            )
        }

        if (value.isNotEmpty() &&
            entryStroke.value > 0f &&
            otpState != OtpState.Success
        ) {
            EntryStroke(
                progress = entryStroke.value,
                color = if (otpState == OtpState.Error) {
                    colors.errorBorder
                } else {
                    colors.entryStroke
                },
                boxSize = boxSize
            )
        }
    }
}
