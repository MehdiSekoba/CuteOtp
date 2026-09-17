# CuteOtp

> A beautiful, animated, and customizable OTP input component for Android Jetpack Compose.

<p align="center">
  <img src="ui.gif" width="420" alt="CuteOtp demo" />
</p>

<p align="center">
  <a href="https://github.com/MehdiSekoba/CuteOtp">
    <img src="https://img.shields.io/github/stars/MehdiSekoba/CuteOtp?style=flat" alt="GitHub stars" />
  </a>
  <a href="https://central.sonatype.com/artifact/io.github.mehdisekoba/cuteotp">
    <img src="https://img.shields.io/maven-central/v/io.github.mehdisekoba/cuteotp?style=flat" alt="Maven Central" />
  </a>
  <a href="https://github.com/MehdiSekoba/CuteOtp/blob/main/LICENSE">
    <img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="License" />
  </a>
</p>

## ✨ Features

* 🎨 Fully customizable OTP boxes
* ✨ Animated digit entry with moving stroke
* 🎯 Animated success state with orbit effect
* ✅ Error and success states
* 🌙 Light & dark presets
* 📐 Custom size, spacing, shape and border
* ⌨️ Automatic focus & keyboard handling
* ♿ Accessibility support
* 📱 SMS Retriever API support
* 🔢 Supports 4–8 digit OTPs
* 🧩 Built for Jetpack Compose & Material 3

## 📦 Installation

### Version Catalog

```toml
[versions]
cuteOtp = "1.0.1"

[libraries]
cuteotp = { module = "io.github.mehdisekoba:cuteotp", version.ref = "cuteOtp" }
```

```kotlin
implementation(libs.cuteotp)
```

### Without Version Catalog

```kotlin
implementation("io.github.mehdisekoba:cuteotp:1.0.1")
```

## 🚀 Basic Usage

```kotlin
var otpState by remember { mutableStateOf(OtpState.Default) }

CuteOtpInput(
    otpLength = 4,
    otpState = otpState,
    onOtpComplete = { otp ->
        if (otp == "4719") {
            otpState = OtpState.Success
        } else {
            otpState = OtpState.Error
        }
    }
)
```

## 🎨 Customization

```kotlin
CuteOtpInput(
    otpLength = 6,
    boxSize = 58.dp,
    boxSpacing = 10.dp,
    borderWidth = 1.5.dp,
    shape = RoundedCornerShape(16.dp),
    colors = OtpColors.Light,
    textStyle = LocalTextStyle.current.copy(
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
    )
)
```

### Predefined Styles

```kotlin
OtpStyles.Light()
OtpStyles.Dark()
OtpStyles.StyleOtp()
```

## 🔄 States

```kotlin
OtpState.Default
OtpState.Error
OtpState.Success
```

`OtpState.Success` displays the animated success transition.

## 📱 SMS Retriever

CuteOtp supports Google's SMS Retriever API without requiring `READ_SMS` permission.

```kotlin
OtpSmsRetrieverEffect(
    otpLength = 4,
    onOtpReceived = { code, message, sender ->
        // Handle received OTP
    },
    onTimeout = {
        // Handle timeout
    }
)
```

For SMS Retriever to work, your SMS should contain the OTP and your app's **11-character app hash**.

Generate the hash with:

```kotlin
val helper = AppSignatureHelper(this)
val hashes = helper.getAppSignatures()

//Generate Hashcode
hashes.forEach {
    Log.d("HashcodeLogs", "Hashcode: $it")
}
```

> ⚠️ Debug and release builds may have different hashes because they can use different signing certificates.

## 🧩 Requirements

* Android API 24+
* Kotlin 2.4.10
* Jetpack Compose
* Material 3
* Compose BOM 2026.08.00
* Compile SDK 37
* Android Gradle Plugin 9.4.0

## 📄 License

```text
Copyright 2026 Mehdi Sekoba

Licensed under the Apache License, Version 2.0.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0
```

See the [LICENSE](LICENSE) file for details.
