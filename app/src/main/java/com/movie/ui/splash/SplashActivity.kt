package com.movie.ui.splash

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.movie.BaseActivity
import com.movie.MainApp
import com.movie.R
import com.movie.databinding.ActivitySplashBinding
import com.movie.ui.home.HomeActivity
import javax.inject.Inject

class SplashActivity: BaseActivity(), SplashView {

    @Inject
    lateinit var presenter: SplashPresenter

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainApp.create(application).provides().inject(activity = this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        presenter.set(this)
    }

    override fun onResume() {
        super.onResume()
        presenter.startTimer()
    }

    override fun onPause() {
        super.onPause()
        presenter.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.dispose()
    }

    override fun onClose() {
        HomeActivity.create().start(context = this)
        fadeIn()
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        fadeIn()
    }
}