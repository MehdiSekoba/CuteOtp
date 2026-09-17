package ir.mehdisekoba.cuteotp.internal.boxes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * EntryStroke draws an animated border effect for an OTP input box.
 * It renders a static light background stroke and a moving dashed foreground stroke.
 *
 * @param progress A float value (typically 0.0 to 1.0) representing the animation state.
 * @param color The base color used for the strokes.
 * @param boxSize The size of the box to draw the stroke around.
 */
@Composable
internal fun EntryStroke(
    progress: Float,
    color: Color,
    boxSize: Dp
) {
    val density = LocalDensity.current
    val strokeWidth = with(density) { 2.dp.toPx() }
    val inset = with(density) { 2.25.dp.toPx() }
    val radius = with(density) {
        (14.dp.toPx()).coerceAtMost(boxSize.toPx() / 2f - inset)
    }

    Canvas(Modifier.size(boxSize)) {
        val left = inset
        val top = inset
        val right = size.width - inset
        val bottom = size.height - inset
        val width = right - left
        val height = bottom - top
        val r = radius.coerceAtMost(minOf(width, height) / 2f)

        val straight = 2f * ((width - 2f * r) + (height - 2f * r))
        val curved = 2f * Math.PI.toFloat() * r
        val perimeter = (straight + curved).coerceAtLeast(1f)

        val fade = if (progress <= 1f) {
            1f
        } else {
            (1.22f - progress) / 0.22f
        }.coerceIn(0f, 1f)

        drawRoundRect(
            color = color.copy(alpha = 0.12f * fade),
            topLeft = androidx.compose.ui.geometry.Offset(left, top),
            size = androidx.compose.ui.geometry.Size(width, height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r),
            style = Stroke(width = strokeWidth * 0.75f)
        )

        val segment = (perimeter * 0.27f).coerceAtLeast(strokeWidth * 2f)
        val gap = (perimeter - segment).coerceAtLeast(strokeWidth)
        val phase = -perimeter * progress.coerceIn(0f, 1f)

        drawRoundRect(
            color = color.copy(alpha = 0.96f * fade),
            topLeft = androidx.compose.ui.geometry.Offset(left, top),
            size = androidx.compose.ui.geometry.Size(width, height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r),
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(segment, gap),
                    phase = phase
                )
            )
        )
    }
}
