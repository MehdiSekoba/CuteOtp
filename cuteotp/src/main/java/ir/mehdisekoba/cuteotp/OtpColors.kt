package ir.mehdisekoba.cuteotp

import androidx.compose.ui.graphics.Color

data class OtpColors(
    val unfocusedBorder: Color = Color(0xFF3A3A3C),
    val focusedBorder: Color = Color(0xFFE5E5EA),
    val filledBorder: Color = Color(0xFF636366),
    val errorBorder: Color = Color(0xFFFF453A),
    val successBorder: Color = Color(0xFF32D74B),
    val unfocusedBackground: Color = Color(0xFF1C1C1E),
    val focusedBackground: Color = Color(0xFF2C2C2E),
    val filledBackground: Color = Color(0xFF2C2C2E),
    val errorBackground: Color = Color(0xFF2C1515),
    val successBackground: Color = Color(0xFF0A2A12),
    val text: Color = Color.White,
    val errorText: Color = Color(0xFFFF453A),
    val successText: Color = Color(0xFF32D74B),
    val cursorColor: Color = Color(0xFFE5E5EA),
    val entryStroke: Color = Color.White
) {
    companion object {
        val Default = OtpColors()

        val Light = OtpColors(
            unfocusedBorder = Color(0xFFD1D1D6),
            focusedBorder = Color(0xFF007AFF),
            filledBorder = Color(0xFF8E8E93),
            errorBorder = Color(0xFFFF3B30),
            successBorder = Color(0xFF34C759),
            unfocusedBackground = Color(0xFFF2F2F7),
            focusedBackground = Color.White,
            filledBackground = Color.White,
            errorBackground = Color(0xFFFFF2F2),
            successBackground = Color(0xFFF2FFF5),
            text = Color.Black,
            errorText = Color(0xFFFF3B30),
            successText = Color(0xFF34C759),
            cursorColor = Color(0xFF007AFF),
            entryStroke = Color(0xFF007AFF)
        )

        val OtpStyle = OtpColors()
    }
}
