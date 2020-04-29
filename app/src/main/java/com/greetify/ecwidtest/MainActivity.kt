package com.greetify.ecwidtest

import android.graphics.Paint
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(ProductSurfaceView(this))

    }
}
