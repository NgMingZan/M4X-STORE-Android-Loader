package store.m4x.loader

import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    companion object {
        // Endpoint riêng của M4X STORE. Không chứa mật khẩu admin.
        private const val VERIFY_URL = "https://m4x-key.pages.dev/verify"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val keyInput = findViewById<EditText>(R.id.keyInput)
        val deviceText = findViewById<TextView>(R.id.deviceText)
        val statusText = findViewById<TextView>(R.id.statusText)
        val verifyButton = findViewById<Button>(R.id.verifyButton)
        val continueButton = findViewById<Button>(R.id.continueButton)

        val deviceId = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown"

        deviceText.text = "Device ID: $deviceId"

        verifyButton.setOnClickListener {
            val key = keyInput.text.toString().trim()
            if (key.isEmpty()) {
                statusText.text = "Nhập key trước."
                return@setOnClickListener
            }

            verifyButton.isEnabled = false
            statusText.text = "Đang kiểm tra..."
            continueButton.visibility = View.GONE

            thread {
                try {
                    val result = verifyKey(key, deviceId)
                    runOnUiThread {
                        if (result.optBoolean("valid", false)) {
                            statusText.text =
                                "✓ Key hợp lệ\nHết hạn: ${result.optString("expires_at", "")}"
                            continueButton.visibility = View.VISIBLE
                        } else {
                            statusText.text =
                                "✕ ${result.optString("error", "Key không hợp lệ")}"
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        statusText.text = "✕ Lỗi kết nối: ${e.message}"
                    }
                } finally {
                    runOnUiThread {
                        verifyButton.isEnabled = true
                    }
                }
            }
        }

        continueButton.setOnClickListener {
            statusText.text = "M4X STORE đã xác thực.\nLoader sẵn sàng."
        }
    }

    private fun verifyKey(key: String, deviceId: String): JSONObject {
        val connection = (URL(VERIFY_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10000
            readTimeout = 10000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
        }

        val body = JSONObject()
            .put("key", key)
            .put("device_id", deviceId)
            .toString()

        connection.outputStream.use {
            it.write(body.toByteArray(Charsets.UTF_8))
        }

        val stream =
            if (connection.responseCode in 200..299)
                connection.inputStream
            else
                connection.errorStream

        val response = stream.bufferedReader().use { it.readText() }
        connection.disconnect()

        return JSONObject(response)
    }
}
