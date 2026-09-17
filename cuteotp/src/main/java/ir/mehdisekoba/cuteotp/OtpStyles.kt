package ir.mehdisekoba.cuteotp

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object OtpStyles {

    @Composable
    fun StyleOtp(
        otpLength: Int = 4,
        value: String? = null,
        onOtpComplete: (String) -> Unit = {},
        onOtpChange: (String) -> Unit = {},
        modifier: Modifier = Modifier,
        otpState: OtpState = OtpState.Default,
        colors: OtpColors = OtpColors.OtpStyle
    ) {
        CuteOtpInput(
            otpLength = otpLength,
            value = value,
            onOtpComplete = onOtpComplete,
            onOtpChange = onOtpChange,
            modifier = modifier,
            otpState = otpState,
            colors = colors,
            boxSize = 56.dp,
            boxSpacing = 12.dp,
            shape = RoundedCornerShape(14.dp),
            borderWidth = 1.25.dp,
            enableSuccessAnimation = true,
            successAnimationDuration = 3500
        )
    }

    @Composable
    fun Dark(
        otpLength: Int = 4,
        value: String? = null,
        onOtpComplete: (String) -> Unit = {},
        onOtpChange: (String) -> Unit = {},
        modifier: Modifier = Modifier,
        otpState: OtpState = OtpState.Default,
        enableSuccessAnimation: Boolean = true,
        colors: OtpColors = OtpColors.Default
    ) {
        CuteOtpInput(
            otpLength = otpLength,
            value = value,
            onOtpComplete = onOtpComplete,
            onOtpChange = onOtpChange,
            modifier = modifier,
            otpState = otpState,
            colors = colors,
            boxSize = 56.dp,
            boxSpacing = 10.dp,
            shape = RoundedCornerShape(14.dp),
            borderWidth = 1.25.dp,
            enableSuccessAnimation = enableSuccessAnimation
        )
    }

    @Composable
    fun Light(
        otpLength: Int = 4,
        value: String? = null,
        onOtpComplete: (String) -> Unit = {},
        onOtpChange: (String) -> Unit = {},
        modifier: Modifier = Modifier,
        otpState: OtpState = OtpState.Default,
        enableSuccessAnimation: Boolean = true,
        colors: OtpColors = OtpColors.Light
    ) {
        CuteOtpInput(
            otpLength = otpLength,
            value = value,
            onOtpComplete = onOtpComplete,
            onOtpChange = onOtpChange,
            modifier = modifier,
            otpState = otpState,
            colors = colors,
            boxSize = 56.dp,
            boxSpacing = 10.dp,
            shape = RoundedCornerShape(14.dp),
            borderWidth = 1.25.dp,
            enableSuccessAnimation = enableSuccessAnimation
        )
    }
}
