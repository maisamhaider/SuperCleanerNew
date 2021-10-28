package com.example.junckcleaner.views.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.junckcleaner.R
import com.example.junckcleaner.annotations.MyAnnotations

class DeleteActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete)

        refreshAd(findViewById(R.id.fl_adplaceholder))
        val type = intent.getStringExtra(MyAnnotations.SCAN_TYPE)

        val textViewHeadingFinished = findViewById<TextView>(R.id.textView_heading_finished)

        findViewById<ImageView>(R.id.imageView_back).setOnClickListener { finish() }

        val view22 = findViewById<View>(R.id.view22)
        when (type) {
            MyAnnotations.IMAGES -> {
                window.statusBarColor = ContextCompat.getColor(this, R.color.color_yellow_sea_1)
                view22.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.shape_header_duplicate_iamge
                )
                textViewHeadingFinished.text = "Duplicate Images"

            }
            MyAnnotations.VIDEOS -> {
                window.statusBarColor = ContextCompat.getColor(this, R.color.color_cerulean_2)
                view22.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.shape_header_duplicate_videos
                )
                textViewHeadingFinished.text = "Duplicate Videos"

            }
            MyAnnotations.AUDIOS -> {
                window.statusBarColor = ContextCompat.getColor(this, R.color.color_clementine_1)
                view22.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.shape_header_duplicate_audios
                )
                textViewHeadingFinished.text = "Duplicate Audios"
            }
            MyAnnotations.DOCUMENTS -> {
                window.statusBarColor = ContextCompat.getColor(this, R.color.color_maroon_flush_1)
                view22.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.shape_header_duplicate_docs
                )
                textViewHeadingFinished.text = "Duplicate Documents"

            }
            MyAnnotations.ALL_SCAN -> {
                window.statusBarColor = ContextCompat.getColor(this, R.color.color_main)
                view22.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.shape_gradient_maincolor_1
                )
                textViewHeadingFinished.text = "Deep Clean"

            }
        }
    }


}