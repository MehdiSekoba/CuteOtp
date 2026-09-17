package ir.mehdisekoba.sample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.mehdisekoba.sample.data.data.LocalOtpDataSource
import ir.mehdisekoba.sample.data.model.BodyLogin
import ir.mehdisekoba.sample.data.repository.LoginRepository
import ir.mehdisekoba.sample.utils.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for handling Login and OTP Verification logic.
 *
 * This class manages the state of login requests and verification requests using [NetworkRequest].
 * It interacts with [LoginRepository] to perform network operations and updates the UI state
 * via [MutableStateFlow].
 */
class LoginViewModel : ViewModel() {

    private val repository = LoginRepository(
        localDataSource = LocalOtpDataSource()
    )

    /**
     * State tracking the progress and result of the login (OTP request) API call.
     */
    private val _loginData =
        MutableStateFlow<NetworkRequest<Unit>?>(
            null
        )

    /**
     * Exposed flow for observing login state changes in the UI.
     */
    val loginData =
        _loginData.asStateFlow()

    /**
     * State tracking the progress and result of the OTP verification API call.
     */
    private val _verifyData =
        MutableStateFlow<NetworkRequest<Unit>?>(
            null
        )

    /**
     * Exposed flow for observing verification state changes in the UI.
     */
    val verifyData =
        _verifyData.asStateFlow()

    /**
     * Triggers the login API call to send an OTP to the user.
     * @param body Contains login credentials (e.g., phone number).
     */
    fun callLoginApi(body: BodyLogin) {
        viewModelScope.launch {

            _loginData.value =
                NetworkRequest.Loading()

            repository
                .postLogin(body)
                .onSuccess {
                    _loginData.value =
                        NetworkRequest.Success(Unit)
                }
                .onFailure { exception ->
                    _loginData.value =
                        NetworkRequest.Error(
                            exception.message
                                ?: "Failed to send verification code."
                        )
                }
        }
    }

    /**
     * Triggers the verification API call to validate the OTP code.
     * @param body Contains verification details (e.g., phone number and code).
     */
    fun callVerifyApi(body: BodyLogin) {
        viewModelScope.launch {

            _verifyData.value =
                NetworkRequest.Loading()

            repository
                .postVerify(body)
                .onSuccess {
                    _verifyData.value =
                        NetworkRequest.Success(Unit)
                }
                .onFailure { exception ->
                    _verifyData.value =
                        NetworkRequest.Error(
                            exception.message
                                ?: "Invalid verification code."
                        )
                }
        }
    }
}




