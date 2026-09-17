package ir.mehdisekoba.cuteotp.internal.animation

internal fun lerp(
    start: Float,
    stop: Float,
    fraction: Float
): Float = start + (stop - start) * fraction.coerceIn(0f, 1f)

internal fun inverseLerp(
    start: Float,
    stop: Float,
    value: Float
): Float {
    if (start == stop) return 1f
    return ((value - start) / (stop - start)).coerceIn(0f, 1f)
}

internal fun smoothStep(value: Float): Float {
    val t = value.coerceIn(0f, 1f)
    return t * t * (3f - 2f * t)
}
