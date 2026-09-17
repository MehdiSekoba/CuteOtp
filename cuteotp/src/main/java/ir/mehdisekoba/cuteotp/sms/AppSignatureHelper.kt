package ir.mehdisekoba.cuteotp.sms

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Arrays

/**
 * Official-style helper to compute the 11-character app hash
 * that must be included in the SMS body for SmsRetriever to work.
 *
 * How to use:
 * 1. Call `AppSignatureHelper(context).getAppSignatures()` once in debug
 * 2. Take the returned 11-char string
 * 3. Tell your backend to append it at the end of the SMS, e.g.:
 *
 *    <#> Your code is 4719
 *    FA+9qCX9VSu
 *
 * Important: the hash is different for debug and release keystores.
 */
class AppSignatureHelper(private val context: Context) {

    companion object {
        private const val TAG = "AppSignatureHelper"
        private const val HASH_TYPE = "SHA-256"
        private const val NUM_HASHED_BYTES = 9
        private const val NUM_BASE64_CHAR = 11
    }

    /**
     * Returns list of possible app hashes (usually one).
     */
    fun getAppSignatures(): List<String> {
        val signatures = mutableListOf<String>()
        try {
            val packageName = context.packageName
            val packageManager = context.packageManager

            val packageInfo = if (Build.VERSION.SDK_INT >= 28) {
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES
                )
            }

            val signingSignatures = if (Build.VERSION.SDK_INT >= 28) {
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            signingSignatures?.forEach { signature ->
                val hash = hash(packageName, signature.toCharsString())
                if (hash != null) {
                    signatures.add(hash)
                    Log.d(TAG, "App hash: $hash")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get app signatures", e)
        }
        return signatures
    }

    private fun hash(packageName: String, signature: String): String? {
        val appInfo = "$packageName $signature"
        try {
            val messageDigest = MessageDigest.getInstance(HASH_TYPE)
            messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
            var hashSignature = messageDigest.digest()

            // Truncate
            hashSignature = hashSignature.copyOfRange(0, NUM_HASHED_BYTES)

            // Encode and take first 11 chars
            var base64Hash = Base64.encodeToString(
                hashSignature,
                Base64.NO_PADDING or Base64.NO_WRAP
            )
            base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR)

            return base64Hash
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "Hash algorithm not found", e)
        }
        return null
    }
}
