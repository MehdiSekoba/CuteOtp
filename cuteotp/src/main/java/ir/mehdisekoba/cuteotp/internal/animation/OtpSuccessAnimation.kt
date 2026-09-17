package ir.mehdisekoba.cuteotp.internal.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ir.mehdisekoba.cuteotp.OtpColors
import ir.mehdisekoba.cuteotp.OtpState
import ir.mehdisekoba.cuteotp.internal.boxes.OtpBox
import kotlin.math.cos
import kotlin.math.sin

/**
 * Composable that handles the success animation sequence for the OTP verification.
 * It animates a progress value from 0f to 1f over a set duration, split into distinct keyframe phases
 * (gathering, settling, orbiting, collapsing, and final success confirmation).
 *
 * @param value The full OTP text value.
 * @param otpLength The number of OTP characters/boxes.
 * @param colors The color configuration for the OTP boxes and borders.
 * @param textStyle The style applied to the text inside the OTP boxes.
 * @param boxSize The width and height of each individual OTP box.
 * @param boxSpacing The horizontal spacing between neighboring boxes.
 * @param shape The shape of each OTP box.
 * @param borderWidth The stroke width of the borders.
 * @param durationMillis The total animation duration in milliseconds.
 */
@Composable
internal fun OtpSuccessAnimation(
    value: String,
    otpLength: Int,
    colors: OtpColors,
    textStyle: TextStyle,
    boxSize: Dp,
    boxSpacing: Dp,
    shape: androidx.compose.ui.graphics.Shape,
    borderWidth: Dp,
    durationMillis: Int,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val progress = remember { Animatable(0f) }

    LaunchedEffect(durationMillis) {
        progress.snapTo(0f)
        val duration = durationMillis.coerceAtLeast(2200)

        progress.animateTo(
            1f,
            keyframes {
                this.durationMillis = duration
                0f at 0
                0.24f at (duration * 0.24f).toInt() using FastOutSlowInEasing
                0.34f at (duration * 0.34f).toInt() using FastOutSlowInEasing
                0.76f at (duration * 0.76f).toInt() using LinearEasing
                0.88f at (duration * 0.88f).toInt() using FastOutSlowInEasing
                1f at duration using FastOutSlowInEasing
            }
        )
    }

    SuccessOrbit(
        value = value,
        otpLength = otpLength,
        progress = progress.value,
        colors = colors,
        textStyle = textStyle,
        boxSize = boxSize,
        boxSpacing = boxSpacing,
        shape = shape,
        borderWidth = borderWidth,
        density = density,
        enabled = enabled,
        modifier = modifier
    )
}

/**
 * Renders the orbit-style animation for the successful OTP entry.
 * The animation transitions through five distinct phases based on the normalized [progress]:
 * 1. **Gathering** (0.00 to 0.24): Boxes migrate from a horizontal row layout into a circular arrangement.
 * 2. **Settle** (0.24 to 0.34): Boxes transition precisely into their orbit trajectories while a dashed ring emerges.
 * 3. **Orbit** (0.34 to 0.76): The boxes rotate collectively around the center with slight individual self-rotations.
 * 4. **Collapse** (0.76 to 0.88): The circle collapses inward into a tight cluster, changing state to [OtpState.Success].
 * 5. **Success** (0.88 to 1.00): The boxes scale down to 0, and the final [SuccessTile] checkmark scales up into view.
 *
 * @param progress The current normalized animation progress between 0.0 and 1.0.
 * @param density The screen density used to calculate coordinate transformations and pixel lengths.
 * @param enabled Whether the component interactions are enabled during animation.
 * @param modifier The modifier to be applied to the outer layout container.
 */
@Composable
private fun SuccessOrbit(
    value: String,
    otpLength: Int,
    progress: Float,
    colors: OtpColors,
    textStyle: TextStyle,
    boxSize: Dp,
    boxSpacing: Dp,
    shape: androidx.compose.ui.graphics.Shape,
    borderWidth: Dp,
    density: androidx.compose.ui.unit.Density,
    enabled: Boolean,
    modifier: Modifier
) {
    val p = progress.coerceIn(0f, 1f)

    val gathering = inverseLerp(0f, 0.24f, p)
    val settle = inverseLerp(0.24f, 0.34f, p)
    val orbit = inverseLerp(0.34f, 0.76f, p)
    val collapse = inverseLerp(0.76f, 0.88f, p)
    val success = inverseLerp(0.88f, 1f, p)

    val smoothGathering = smoothStep(gathering)
    val smoothSettle = smoothStep(settle)
    val smoothCollapse = smoothStep(collapse)

    val orbitRadiusPx = with(density) { (boxSize * 1.38f).toPx() }
    val clusterRadiusPx = with(density) { (boxSize * 0.68f).toPx() }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val orbitRotationDeg = orbit * 360f
        val orbitRotationRad = Math.toRadians(orbitRotationDeg.toDouble())

        val ringAlpha = when {
            p <= 0.24f -> 0f
            p <= 0.76f -> (smoothSettle * 0.34f).coerceAtLeast(0.01f)
            p <= 0.88f -> (1f - smoothCollapse) * 0.28f
            else -> 0f
        }

        if (ringAlpha > 0f) {
            Canvas(Modifier.size(boxSize * 3.4f)) {
                drawCircle(
                    color = colors.successBorder.copy(alpha = ringAlpha),
                    radius = orbitRadiusPx,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = with(density) { 1.2.dp.toPx() },
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(
                                with(density) { 5.dp.toPx() },
                                with(density) { 7.dp.toPx() }
                            ),
                            0f
                        )
                    )
                )
            }
        }

        repeat(otpLength) { index ->
            val char = value.getOrNull(index)?.toString().orEmpty()

            val totalWidthPx = with(density) {
                (boxSize * otpLength + boxSpacing * (otpLength - 1)).toPx()
            }

            val rowX = with(density) {
                (boxSize * index + boxSpacing * index + boxSize / 2f).toPx() -
                    totalWidthPx / 2f
            }

            val step = 360f / otpLength
            val targetAngleDeg = -180f + step * index
            val targetAngleRad = Math.toRadians(targetAngleDeg.toDouble())

            val stagingAngleDeg = targetAngleDeg - 24f + index * 2.5f
            val stagingRad = Math.toRadians(stagingAngleDeg.toDouble())
            val stagingRadius = orbitRadiusPx * 0.86f

            val stagingX = cos(stagingRad).toFloat() * stagingRadius
            val stagingY = sin(stagingRad).toFloat() * stagingRadius

            val baseOrbitX = cos(targetAngleRad).toFloat() * orbitRadiusPx
            val baseOrbitY = sin(targetAngleRad).toFloat() * orbitRadiusPx

            val orbitX =
                baseOrbitX * cos(orbitRotationRad).toFloat() -
                    baseOrbitY * sin(orbitRotationRad).toFloat()

            val orbitY =
                baseOrbitX * sin(orbitRotationRad).toFloat() +
                    baseOrbitY * cos(orbitRotationRad).toFloat()

            val clusterAngleDeg =
                targetAngleDeg + 90f +
                    (index - (otpLength - 1) / 2f) * 16f

            val clusterRad = Math.toRadians(clusterAngleDeg.toDouble())
            val individualClusterRadius =
                clusterRadiusPx * (0.82f + index * 0.06f)

            val clusterX =
                cos(clusterRad).toFloat() * individualClusterRadius
            val clusterY =
                sin(clusterRad).toFloat() * individualClusterRadius

            val x = when {
                p < 0.24f -> lerp(rowX, stagingX, smoothGathering)
                p < 0.34f -> lerp(stagingX, baseOrbitX, smoothSettle)
                p < 0.76f -> orbitX
                else -> lerp(orbitX, clusterX, smoothCollapse)
            }

            val y = when {
                p < 0.24f -> lerp(0f, stagingY, smoothGathering)
                p < 0.34f -> lerp(stagingY, baseOrbitY, smoothSettle)
                p < 0.76f -> orbitY
                else -> lerp(orbitY, clusterY, smoothCollapse)
            }

            val gatheringRotation = lerp(
                0f,
                -8f + index * 4f,
                smoothGathering
            )

            val orbitalSelfRotation =
                orbitRotationDeg * when (index % 4) {
                    0 -> 0.18f
                    1 -> -0.12f
                    2 -> 0.16f
                    else -> -0.14f
                }

            val collapseRotation = lerp(
                orbitalSelfRotation,
                orbitalSelfRotation +
                    when (index % 4) {
                        0 -> -22f
                        1 -> 18f
                        2 -> -16f
                        else -> 24f
                    },
                smoothCollapse
            )

            val finalRotation = gatheringRotation +
                if (p < 0.76f) orbitalSelfRotation else collapseRotation

            val scale = when {
                p < 0.76f -> 1f
                p < 0.88f -> lerp(1f, 0.72f, smoothCollapse)
                else -> lerp(0.72f, 0f, smoothStep(success))
            }

            OtpBox(
                value = char,
                isFocused = false,
                isFilled = char.isNotEmpty(),
                otpState = if (p >= 0.76f) OtpState.Success else OtpState.Default,
                colors = colors,
                textStyle = textStyle,
                boxSize = boxSize,
                shape = shape,
                borderWidth = borderWidth,
                index = index,
                otpLength = otpLength,
                onClick = {},
                modifier = Modifier.graphicsLayer {
                    translationX = x
                    translationY = y
                    rotationZ = finalRotation
                    scaleX = scale
                    scaleY = scale
                }
            )
        }

        if (success > 0f) {
            SuccessTile(
                progress = smoothStep(success),
                boxSize = boxSize,
                shape = shape,
                borderWidth = borderWidth,
                colors = colors,
                density = density
            )
        }
    }
}
