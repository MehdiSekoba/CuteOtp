package ir.mehdisekoba.sample.data.repository

import ir.mehdisekoba.sample.data.data.LocalOtpDataSource
import ir.mehdisekoba.sample.data.model.BodyLogin
class LoginRepository(
    private val localDataSource: LocalOtpDataSource
) {

    suspend fun postLogin(
        body: BodyLogin
    ): Result<Unit> {
        return localDataSource.postLogin(body)
    }

    suspend fun postVerify(
        body: BodyLogin
    ): Result<Unit> {
        return localDataSource.postVerify(body)
    }
}