package ir.mehdisekoba.sample.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mehdisekoba.sample.data.model.BodyLogin
import ir.mehdisekoba.sample.utils.NetworkRequest
import ir.mehdisekoba.sample.viewmodel.LoginViewModel


@Composable
fun SignupPhone(
    viewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current

    val loginState by viewModel.loginData
        .collectAsStateWithLifecycle()

    var phone by rememberSaveable {
        mutableStateOf("")
    }

    var showVerify by rememberSaveable {
        mutableStateOf(false)
    }

    val isLoading =
        loginState is NetworkRequest.Loading

    /*
     * Handle login result
     */
    LaunchedEffect(loginState) {
        when (val state = loginState) {

            is NetworkRequest.Success -> {
                showVerify = true
            }

            is NetworkRequest.Error -> {
                Toast.makeText(
                    context,
                    state.error ?: "An error occurred.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> Unit
        }
    }

    if (showVerify) {

        PhoneVerify(
            phone = phone,
            viewModel = viewModel,
            onSuccess = {
                // Navigation to Home
            }
        )

    } else {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0xFF376AED)
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            top = 48.dp,
                            bottom = 32.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Create your account",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "Enter your mobile number to receive a verification code.",
                        modifier = Modifier.padding(
                            horizontal = 24.dp
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

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
                        .verticalScroll(
                            rememberScrollState()
                        )
                        .padding(
                            horizontal = 24.dp,
                            vertical = 30.dp
                        )
                ) {

                    OutlinedTextField(
                        value = phone,

                        onValueChange = { newValue ->
                            phone = newValue
                                .filter { it.isDigit() }
                                .take(11)
                        },

                        modifier = Modifier.fillMaxWidth(),

                        enabled = !isLoading,

                        singleLine = true,

                        label = {
                            Text("Mobile number")
                        },

                        placeholder = {
                            Text("09123456789")
                        },

                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Phone,
                                contentDescription = null
                            )
                        },

                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),

                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (
                                    phone.length == 11 &&
                                    !isLoading
                                ) {
                                    viewModel.callLoginApi(
                                        BodyLogin(
                                            login = phone
                                        )
                                    )
                                }
                            }
                        ),

                        shape = RoundedCornerShape(18.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 4.dp,
                                vertical = 6.dp
                            ),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "${phone.length}/11",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF8A8A8A)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    Button(
                        onClick = {

                            if (phone.length == 11) {

                                viewModel.callLoginApi(
                                    BodyLogin(
                                        login = phone
                                    )
                                )

                            } else {

                                Toast.makeText(
                                    context,
                                    "The mobile number must be 11 digits long.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),

                        enabled =
                            phone.length == 11 &&
                                    !isLoading,

                        shape = RoundedCornerShape(18.dp),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF376AED),
                            disabledContainerColor = Color(0xFFE5E7EB)
                        )
                    ) {

                        AnimatedContent(
                            targetState = isLoading,
                            label = "login_button_content"
                        ) { loading ->

                            if (loading) {

                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.5.dp,
                                    color = Color.White
                                )

                            } else {

                                Row(
                                    verticalAlignment =
                                        Alignment.CenterVertically,
                                    horizontalArrangement =
                                        Arrangement.Center
                                ) {

                                    Text(
                                        text = "Continue",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )

                                    Spacer(
                                        modifier = Modifier.width(8.dp)
                                    )

                                    Icon(
                                        imageVector =
                                            Icons.AutoMirrored.Rounded.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Text(
                        text = "By continuing, you accept the Terms of Use and Privacy Policy.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF8A8A8A),
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }
    }
}







