package store.m4x.loader

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = TextView(this).apply {
            text = "M4X STORE"
            textSize = 28f
            gravity = android.view.Gravity.CENTER
        }

        setContentView(text)
    }
}
