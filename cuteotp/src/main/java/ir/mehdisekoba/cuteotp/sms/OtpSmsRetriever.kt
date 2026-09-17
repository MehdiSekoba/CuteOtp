package ir.mehdisekoba.cuteotp.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.BundleCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

/**
 * SMS Retriever helper based on the official Android documentation:
 * https://developer.android.com/identity/sms-retriever
 *
 * Flow:
 * 1. Call [start] (or use [OtpSmsRetrieverEffect]) when the OTP screen is shown.
 * 2. Your server sends an SMS that contains a one-time code **and** your 11-char app hash.
 * 3. Google Play services delivers the message to your [OtpSmsBroadcastReceiver].
 * 4. Extract the code and pass it to your UI (controlled [value] of CuteOtpInput).
 *
 * No READ_SMS permission is required.
 */
object OtpSmsRetriever {

    private const val TAG = "OtpSmsRetriever"

    /**
     * Starts SmsRetriever. It listens for **one** matching SMS for up to 5 minutes.
     *
     * Call this right before (or right after) you ask your server to send the SMS.
     */
    fun start(
        context: Context,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null,
    ) {
        val client = SmsRetriever.getClient(context)
        val task = client.startSmsRetriever()

        task.addOnSuccessListener {
            Log.d(TAG, "SMS Retriever started successfully")
            onSuccess?.invoke()
        }
        task.addOnFailureListener { e ->
            Log.e(TAG, "Failed to start SMS Retriever", e)
            onFailure?.invoke(e)
        }
    }

    /**
     * Extracts the first sequence of [otpLength] digits from the SMS body.
     * Falls back to any 4–8 digit number if an exact match is not found.
     */
    fun extractOtp(message: String, otpLength: Int = 4): String? {
        val exact = Pattern.compile("(?<!\\d)(\\d{$otpLength})(?!\\d)")
        val exactMatcher = exact.matcher(message)
        if (exactMatcher.find()) return exactMatcher.group(1)

        val fallback = Pattern.compile("(?<!\\d)(\\d{4,8})(?!\\d)")
        val fallbackMatcher = fallback.matcher(message)
        return if (fallbackMatcher.find()) fallbackMatcher.group(1) else null
    }
}

/**
 * BroadcastReceiver that receives the SMS from Google Play services.
 *
 * Register either:
 * - Dynamically with [OtpSmsRetrieverEffect] (recommended for Compose), or
 * - In AndroidManifest.xml (see README).
 *
 * Official extras:
 * - [SmsRetriever.EXTRA_STATUS]
 * - [SmsRetriever.EXTRA_SMS_MESSAGE]
 * - [SmsRetriever.EXTRA_SMS_ORIGINATING_ADDRESS] (optional, GMS 24.20+)
 */
class OtpSmsBroadcastReceiver(
    private val otpLength: Int = 4,
    private val onOtpReceived: (code: String, fullMessage: String, sender: String?) -> Unit,
    private val onTimeout: (() -> Unit)? = null
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION != intent.action) return

        val extras = intent.extras ?: return
        val status = BundleCompat.getParcelable(extras, SmsRetriever.EXTRA_STATUS, Status::class.java) ?: return

        when (status.statusCode) {
            CommonStatusCodes.SUCCESS -> {
                // Optional sender address (available from GMS 24.20 onwards)
                val senderAddress = extras.getString(SmsRetriever.EXTRA_SMS_ORIGINATING_ADDRESS)
                val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE) ?: return

                val otp = OtpSmsRetriever.extractOtp(message, otpLength)
                if (otp != null) {
                    onOtpReceived(otp, message, senderAddress)
                } else {
                    Log.w("OtpSmsRetriever", "SMS received but no OTP found: $message")
                }
            }
            CommonStatusCodes.TIMEOUT -> {
                // Waiting for SMS timed out (5 minutes)
                Log.d("OtpSmsRetriever", "SMS Retriever timed out")
                onTimeout?.invoke()
            }
        }
    }
}

/**
 * Compose-friendly wrapper around the official SMS Retriever flow.
 *
 * - Calls [SmsRetriever.getClient].startSmsRetriever()
 * - Registers [OtpSmsBroadcastReceiver] with the correct action & permission handling
 * - Automatically unregisters when the composable leaves the composition / lifecycle is destroyed
 *
 * Usage:
 * ```
 * OtpSmsRetrieverEffect(
 *     otpLength = 4,
 *     enabled = otpState != OtpState.Success,
 *     onOtpReceived = { code, _, _ -> otpValue = code },
 *     onTimeout = { /* show resend */ }
 * )
 * ```
 */
@Composable
fun OtpSmsRetrieverEffect(
    otpLength: Int = 4,
    enabled: Boolean = true,
    onOtpReceived: (code: String, fullMessage: String, sender: String?) -> Unit,
    onTimeout: (() -> Unit)? = null,
    onStartSuccess: (() -> Unit)? = null,
    onStartFailure: ((Exception) -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    /*
     * Always keep the latest callbacks.
     * This prevents the BroadcastReceiver from holding
     * an old Compose lambda.
     */
    val currentOnOtpReceived by rememberUpdatedState(onOtpReceived)
    val currentOnTimeout by rememberUpdatedState(onTimeout)
    val currentOnStartSuccess by rememberUpdatedState(onStartSuccess)
    val currentOnStartFailure by rememberUpdatedState(onStartFailure)

    DisposableEffect(
        enabled,
        otpLength,
        lifecycleOwner
    ) {
        if (!enabled) {
            return@DisposableEffect onDispose { }
        }

        /*
         * Create receiver with the CURRENT callbacks.
         */
        val receiver = OtpSmsBroadcastReceiver(
            otpLength = otpLength,
            onOtpReceived = { code, fullMessage, sender ->
                Log.d(
                    "OtpSmsRetriever",
                    "OTP RECEIVED = $code"
                )

                currentOnOtpReceived(
                    code,
                    fullMessage,
                    sender
                )
            },
            onTimeout = {
                currentOnTimeout?.invoke()
            }
        )

        val filter = IntentFilter(
            SmsRetriever.SMS_RETRIEVED_ACTION
        )

        try {

            /*
             * IMPORTANT:
             * Register receiver BEFORE starting SMS Retriever.
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                context.registerReceiver(
                    receiver,
                    filter,
                    SmsRetriever.SEND_PERMISSION,
                    null,
                    Context.RECEIVER_EXPORTED
                )

            } else {

                @Suppress("UnspecifiedRegisterReceiverFlag")
                context.registerReceiver(
                    receiver,
                    filter,
                    SmsRetriever.SEND_PERMISSION,
                    null
                )
            }

            Log.d(
                "OtpSmsRetriever",
                "Receiver registered"
            )

            /*
             * NOW start SMS Retriever.
             */
            OtpSmsRetriever.start(
                context = context,
                onSuccess = {
                    Log.d(
                        "OtpSmsRetriever",
                        "SMS Retriever started successfully"
                    )

                    currentOnStartSuccess?.invoke()
                },
                onFailure = { exception ->
                    Log.e(
                        "OtpSmsRetriever",
                        "SMS Retriever start failed",
                        exception
                    )

                    currentOnStartFailure?.invoke(exception)
                }
            )

        } catch (e: Exception) {

            Log.e(
                "OtpSmsRetriever",
                "Failed to register SMS receiver",
                e
            )

            try {
                context.unregisterReceiver(receiver)
            } catch (_: Exception) {
            }
        }

        val observer = object : DefaultLifecycleObserver {

            override fun onDestroy(
                owner: LifecycleOwner
            ) {
                try {
                    context.unregisterReceiver(receiver)

                    Log.d(
                        "OtpSmsRetriever",
                        "Receiver unregistered"
                    )
                } catch (_: Exception) {
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {

            try {
                context.unregisterReceiver(receiver)

                Log.d(
                    "OtpSmsRetriever",
                    "Receiver disposed"
                )
            } catch (_: Exception) {
            }

            lifecycleOwner.lifecycle.removeObserver(
                observer
            )
        }
    }
}
