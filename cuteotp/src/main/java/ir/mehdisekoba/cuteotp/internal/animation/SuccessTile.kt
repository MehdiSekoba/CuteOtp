package ir.mehdisekoba.cuteotp.internal.animation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ir.mehdisekoba.cuteotp.OtpColors
import kotlin.math.cos
import kotlin.math.sin

/**
 * SuccessTile is a composable that represents the visual feedback when an OTP is correctly verified.
 * It renders a scaling box with a checkmark and a burst of particles around it.
 *
 * @param progress The animation progress value ranging from 0.0 to 1.0.
 * @param boxSize The base size of the OTP input tile.
 * @param shape The shape of the tile (corners, etc.).
 * @param borderWidth The width of the tile's border.
 * @param colors The color configuration for the OTP component.
 * @param density The screen density used for unit conversions.
 */
@Composable
internal fun SuccessTile(
    progress: Float,
    boxSize: Dp,
    shape: Shape,
    borderWidth: Dp,
    colors: OtpColors,
    density: Density
) {
    val p = progress.coerceIn(0f, 1f)

    val pulse = if (p < 0.55f) {
        lerp(0.76f, 1.04f, smoothStep(p / 0.55f))
    } else {
        lerp(1.04f, 1f, smoothStep((p - 0.55f) / 0.45f))
    }

    val alpha = smoothStep(p)

    Box(
        modifier = Modifier
            .size(boxSize * 1.06f)
            .scale(pulse)
            .clip(shape)
            .background(colors.successBackground.copy(alpha = alpha * 0.55f))
            .border(
                width = borderWidth + 0.45.dp,
                color = colors.successBorder.copy(alpha = alpha),
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(boxSize * 0.34f)) {
            val stroke = with(density) { 1.7.dp.toPx() }

            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(size.width * 0.30f, size.height * 0.52f)
                lineTo(size.width * 0.45f, size.height * 0.66f)
                lineTo(size.width * 0.72f, size.height * 0.35f)
            }

            drawPath(
                path = path,
                color = Color.White.copy(alpha = alpha * 0.9f),
                style = Stroke(
                    width = stroke,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }

    Canvas(Modifier.size(boxSize * 3.2f)) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        val particleProgress =
            smoothStep(((p - 0.18f) / 0.82f).coerceIn(0f, 1f))

        if (particleProgress <= 0f) return@Canvas

        repeat(12) { i ->
            val angle = Math.toRadians(
                (-90f + i * 30f).toDouble()
            )

            val startRadius = with(density) { boxSize.toPx() * 0.72f }
            val endRadius = with(density) {
                boxSize.toPx() * (1.22f + (i % 3) * 0.12f)
            }

            val radius = lerp(
                startRadius,
                endRadius,
                particleProgress
            )

            drawCircle(
                color = colors.successBorder.copy(
                    alpha = (1f - particleProgress * 0.15f) * p
                ),
                radius = with(density) {
                    if (i % 3 == 0) 2.1.dp.toPx() else 1.35.dp.toPx()
                },
                center = androidx.compose.ui.geometry.Offset(
                    centerX + cos(angle).toFloat() * radius,
                    centerY + sin(angle).toFloat() * radius
                )
            )
        }
    }
}
