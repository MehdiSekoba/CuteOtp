plugins {
    alias(libs.plugins.android.library)
     alias(libs.plugins.maven.publish)
     alias(libs.plugins.dokka)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "ir.mehdisekoba.cuteotp"
    compileSdk = 37
    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.material)

    // Lifecycle for OtpSmsRetrieverEffect
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.common)

    // SMS Retriever API (no READ_SMS permission needed)
    implementation(libs.play.services.auth)
    implementation(libs.play.services.auth.api.phone)

}

mavenPublishing {
    coordinates(
        groupId = "io.github.mehdisekoba",
        artifactId = "CuteOtp",
        version = "1.0.1"
    )

    pom {
        name.set("CuteOtp")
        description.set(" A beautiful, animated, and customizable OTP input component for Android Jetpack Compose.")
        url.set("https://github.com/MehdiSekoba")
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
            }
        }
        developers {
            developer {
                id.set("mehdisekoba")
                name.set("Mehdi Sekoba")
                email.set("sekobamahdi@gmail.com")
            }
        }
        scm {
            url.set("https://github.com/MehdiSekoba/CuteOtp")
            connection.set("scm:git:git://github.com/MehdiSekoba/CuteOtp.git")
            developerConnection.set("scm:git:ssh://git@github.com/MehdiSekoba/CuteOtp.git")
        }
    }
    publishToMavenCentral()
    signAllPublications()
}

