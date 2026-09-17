package ir.mehdisekoba.sample.data.data

import ir.mehdisekoba.sample.data.model.BodyLogin
import ir.mehdisekoba.sample.utils.TEST_OTP
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class LocalOtpDataSource {
    suspend fun postLogin(
        body: BodyLogin
    ): Result<Unit> {
        delay(1000.milliseconds)
        return if (body.login.length == 11) {
            Result.success(Unit)
        } else {
            Result.failure(
                IllegalArgumentException(
                    "The mobile number must be 11 digits long."
                )
            )
        }
    }

    suspend fun postVerify(
        body: BodyLogin
    ): Result<Unit> {
        delay(800.milliseconds)
        return if (body.code == TEST_OTP) {
            Result.success(Unit)
        } else {
            Result.failure(
                IllegalArgumentException(
                    "Invalid verification code."
                )
            )
        }
    }
}



