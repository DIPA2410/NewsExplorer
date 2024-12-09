package com.diparoy.newsexplorer.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.diparoy.newsexplorer.R
import com.diparoy.newsexplorer.databinding.ActivityNewsFullBinding

class NewsFullActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsFullBinding
    private var webView: WebView? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsFullBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = binding.webView
        progressBar = binding.prgssBar

        val backButton = findViewById<ImageView>(R.id.back)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val url = intent.getStringExtra("url")

        val webSettings = webView!!.settings
        webSettings.javaScriptEnabled = true

        webView!!.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar!!.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar!!.visibility = View.GONE
            }
        }
        webView!!.loadUrl(url!!)
    }

    override fun onBackPressed() {
        if (webView!!.canGoBack()) {
            webView!!.goBack()
        } else {
            super.onBackPressed()
        }
    }
}