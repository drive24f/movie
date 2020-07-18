package com.movie

import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity() {

    companion object {
        const val BLANK = ""
    }

    fun fadeIn() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}