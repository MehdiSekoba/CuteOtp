package ir.mehdisekoba.sample.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mehdisekoba.cuteotp.CuteOtpInput
import ir.mehdisekoba.cuteotp.OtpColors
import ir.mehdisekoba.cuteotp.OtpState
import ir.mehdisekoba.sample.data.model.BodyLogin
import ir.mehdisekoba.sample.ui.theme.SampleTheme
import ir.mehdisekoba.sample.utils.NetworkRequest
import ir.mehdisekoba.sample.viewmodel.LoginViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun PhoneVerify(
    phone: String,
    viewModel: LoginViewModel = viewModel(),
    onSuccess: () -> Unit
) {
    val verifyState by viewModel.verifyData
        .collectAsStateWithLifecycle()

    PhoneVerifyContent(
        phone = phone,
        verifyState = verifyState,
        onVerifyCode = { code ->
            viewModel.callVerifyApi(
                BodyLogin(
                    login = phone,
                    code = code.toInt()
                )
            )
        },
        onResendCode = {
            viewModel.callLoginApi(
                BodyLogin(
                    login = phone
                )
            )
        },
        onSuccess = onSuccess
    )
}

@Composable
fun PhoneVerifyContent(
    phone: String,
    verifyState: NetworkRequest<Unit>?,
    onVerifyCode: (String) -> Unit,
    onResendCode: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current

    var otp by rememberSaveable {
        mutableStateOf("")
    }

    var otpState by rememberSaveable {
        mutableStateOf(OtpState.Default)
    }

    val isVerifyLoading =
        verifyState is NetworkRequest.Loading

    /*
     * Handle verify result
     */
    LaunchedEffect(verifyState) {
        when (val state = verifyState) {

            is NetworkRequest.Success -> {
                otpState = OtpState.Success

                // Let CuteOtp success animation finish
                delay(3500.milliseconds)

                onSuccess()
            }

            is NetworkRequest.Error -> {
                otpState = OtpState.Error

                Toast.makeText(
                    context,
                    state.error ?: "Invalid verification code.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF376AED))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {

            /*
             * Header
             */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.dp,
                        vertical = 48.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    imageVector = Icons.Rounded.Security,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Text(
                    text = "Phone verification",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Enter the verification code for",
                    color = Color.White.copy(alpha = 0.8f)
                )

                Text(
                    text = phone,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            /*
             * Verification content
             */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(
                        RoundedCornerShape(
                            topStart = 32.dp,
                            topEnd = 32.dp
                        )
                    )
                    .background(Color.White)
                    .imePadding()
                    .navigationBarsPadding()
                    .padding(
                        horizontal = 24.dp,
                        vertical = 36.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                /*
                 * Local test information
                 */
                Surface(
                    color = Color(0xFFF5F7FF),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Local test code: 4719",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color(0xFF376AED),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                /*
                 * CuteOtp
                 */
                CuteOtpInput(
                    otpLength = 4,

                    value = otp,

                    onOtpChange = { value ->

                        otp = value

                        if (otpState == OtpState.Error) {
                            otpState = OtpState.Default
                        }
                    },

                    onOtpComplete = { code ->

                        if (
                            !isVerifyLoading &&
                            code.length == 4
                        ) {
                            onVerifyCode(code)
                        }
                    },

                    otpState = otpState,

                    colors = OtpColors.Light.copy(
                        focusedBorder = Color(0xFF376AED),
                        entryStroke = Color(0xFF376AED)
                    ),

                    boxSize = 60.dp,
                    boxSpacing = 12.dp,
                    shape = RoundedCornerShape(16.dp),

                    enableSuccessAnimation = true,
                    successAnimationDuration = 3500,

                    enabled = !isVerifyLoading
                )

                Spacer(
                    modifier = Modifier.height(28.dp)
                )

                /*
                 * Status message
                 */
                Text(
                    text = when (otpState) {

                        OtpState.Error ->
                            "The verification code is incorrect."

                        OtpState.Success ->
                            "Verification successful"

                        else ->
                            "Enter the 4-digit code"
                    },

                    color = when (otpState) {

                        OtpState.Error ->
                            Color(0xFFFF3B30)

                        OtpState.Success ->
                            Color(0xFF34C759)

                        else ->
                            Color.Gray
                    }
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                /*
                 * Verify loading
                 */
                if (isVerifyLoading) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF376AED)
                    )

                } else {

                    /*
                     * Resend
                     */
                    TextButton(
                        enabled = otpState != OtpState.Success,

                        onClick = {

                            otp = ""
                            otpState = OtpState.Default

                            onResendCode()
                        }
                    ) {
                        Text(
                            text = "Didn't receive the code? Resend"
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhoneVerifyPreview() {
    SampleTheme {
        PhoneVerifyContent(
            phone = "09123456789",
            verifyState = null,
            onVerifyCode = {},
            onResendCode = {},
            onSuccess = {}
        )
    }
}



