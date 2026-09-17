package ir.mehdisekoba.sample


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ir.mehdisekoba.sample.ui.SignupPhone
import ir.mehdisekoba.sample.ui.theme.SampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleTheme(darkTheme = false, dynamicColor = false) {
                SignupPhone()
            }
        }


    }
}
