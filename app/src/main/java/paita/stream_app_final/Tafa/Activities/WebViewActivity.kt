package paita.stream_app_final.Tafa.Activities

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import paita.stream_app_final.R


class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        initall()
    }

    private fun initall() {
        val w = findViewById<View>(R.id.web) as WebView
        w.loadUrl("https://tafa.co.ke/reset-password")
        w.settings.javaScriptEnabled = true
        w.webViewClient = WebViewClient()
    }
}